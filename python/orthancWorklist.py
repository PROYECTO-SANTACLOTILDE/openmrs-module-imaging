import json
import orthanc
import requests

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
    config = orthanc.GetConfiguration()
    configJson = json.loads(config)
    updateRequestStatusURL = configJson["ImagingUpdateRequestStatus"]

    # Handle new study or new instance events
    if changeType == orthanc.ChangeType.STABLE_STUDY:
        try:
            studyInstanceUID = json.loads(orthanc.RestApiGet("/studies/" + resource)) ["MainDicomTags"]["StudyInstanceUID"]
            response = requests.post(updateRequestStatusURL+"?studyInstanceUID="+studyInstanceUID)
            response.raise_for_status()  # Raise an error for failed requests
            orthanc.LogWarning(f"Successfully updated data: {studyInstanceUID}")
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






    
        
