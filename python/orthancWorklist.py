import json
import orthanc
import requests
import time

# Default API URL
getWorklistURL = "http://localhost:7070/openmrs/ws/rest/v1/imaging/worklist"
updateRequestStatusURL = "http://localhost:7070/openmrs/ws/rest/v1/imaging/updatestatus"

def OnWorkList(answers, query, issuerAet, calledAet):
    # Get query in json format and write it to log
    queryDicom = query.WorklistGetDicomQuery()
    queryJson = json.loads(orthanc.DicomBufferToJson(
        queryDicom, orthanc.DicomToJsonFormat.SHORT, orthanc.DicomToJsonFlags.NONE, 0))
    orthanc.LogWarning('C-FIND worklist request: %s' %
                       json.dumps(queryJson, indent = 4))

    response = requests.get(getWorklistURL)
    responseJson = response.json()

    orthanc.LogWarning('Response by server: %s' % json.dumps(responseJson))

    for dicomJson in responseJson:
        responseDicom = orthanc.CreateDicom(json.dumps(dicomJson), None, orthanc.CreateDicomFlags.NONE)
        orthanc.LogWarning(orthanc.DicomBufferToJson(
            responseDicom, orthanc.DicomToJsonFormat.SHORT, orthanc.DicomToJsonFlags.NONE, 0))

        if query.WorklistIsMatch(responseDicom):
            answers.WorklistAddAnswer(query, responseDicom)

def OnChange(changeType, level, resource):

    # Handle new study or new instance events
    if changeType == orthanc.ChangeType.STABLE_SERIES:
        try:
            stepID = None
            studyInstanceUID = None
            seriesJson = json.loads(orthanc.RestApiGet("/series/" + resource))
            if "PerformedProcedureStepID" in seriesJson:
                stepID = seriesJson["PerformedProcedureStepID"]
            elif len(seriesJson["Instances"])>0:
                instanceUid = seriesJson["Instances"][0]
                instanceJson = json.loads(orthanc.RestApiGet("/instances/" + instanceUid+"/tags?simplify"))
                if "PerformedProcedureStepID" in instanceJson:
                    stepID = instanceJson["PerformedProcedureStepID"]
                if "StudyInstanceUID" in instanceJson:
                    studyInstanceUID = instanceJson["StudyInstanceUID"]
            if stepID:
                orthanc.LogWarning("step ID of stable series found: "+stepID)

            if studyInstanceUID:
                orthanc.LogWarning("StudyInstanceUID found: " + studyInstanceUID)

            response = requests.post(updateRequestStatusURL+"?studyInstanceUID=" + studyInstanceUID
                                     + "&performedProcedureStepID=" + stepID)
            response.raise_for_status()  # Raise an error for failed requests
            orthanc.LogWarning(f"+++++ Successfully updated data: {studyInstanceUID}")
        except requests.RequestException as e:
            orthanc.LogError(f"Failed to update request status: {str(e)}")
    else:
        return None

# Read the API URL from orthanc configuration
# resource dicom file
def readUrlFromOrthancConfig(configItemName):
    config = orthanc.GetConfiguration()
    configJson = json.loads(config)
    url = configJson[configItemName]
    orthanc.LogWarning("Imaging worklist URL: " + url)
    return url

orthanc.RegisterWorklistCallback(OnWorkList)
orthanc.RegisterOnChangeCallback(OnChange)

# Read the API URL from the configuration of Orthanc
getWorklistURL = readUrlFromOrthancConfig("ImagingWorklistURL")
updateRequestStatusURL = readUrlFromOrthancConfig("ImagingUpdateRequestStatus")





    
        
