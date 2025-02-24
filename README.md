[![Build Status](https://travis-ci.org/openmrs/openmrs-module-imaging.svg?branch=master)](https://travis-ci.org/openmrs/openmrs-module-imaging)

OpenMRS Imaging Module
==========================
In order to improve the management of patient image data within OpenMRS, the open source electronic healthcare system,
we have developed an integration between Orthanc PACS and OpenMRS, initially designed for OpenMRS 2.x, the most widely used version of OpenMRS.
We also plan to update the project for OpenMRS 3.x, the next generation electronic medical record (EMR) system. This new module is focused on
simplifying the management of imaging data.

Key features of this module include:

* Orthanc Server Configuration: Set up and configure the connection between OpenMRS and one or multiple Orthanc servers.
* Patient Image Data Visualization: View patient image data in a preview window or using Orthanc's DICOM viewers.
* DICOM File Upload: Upload DICOM files directly in OpenMRS.
* (Planned for Next Release) Worklist Management: Manage worklists directly within OpenMRS.

Watch the video demonstration of the module here:  
[![Watch the video](https://img.youtube.com/vi/248A5wPQNgs/0.jpg)](https://youtu.be/248A5wPQNgs)

## Orthanc Server Configuration
The purpose of this dialog is to configure the connection between OpenMRS and one or multiple Orthanc servers.

## Image Data Management
This is the heart of the Orthanc integration, allowing browsing and viewing of patient images through DICOM viewers available within Orthanc.
The module retrieves the metadata of image studies stored on Orthan servers. A mapping function helps associating OpenMRS patient records with their
corresponding studies. In addition, image data can be uploaded directly from the OpenMRS web client to Orthanc servers.

## Radiology Worklist (Under development)
In the context of radiology, a worklist is a list of imaging studies or tasks that a radiologist needs to execute, review, or analyze.
These tasks are typically retrieved from a radiology information system (RIS), a specialized database that manages patient and imaging information.
However, in situations where an RIS system is not available or feasible (such as for smaller healthcare facilities, clinics, or specific locations),
a simple radiology worklist can be sufficient.

Deployment of the new module

* Deploy OpenMRS Imaging module from it's directory by cloning the repository, navigating to the directory and running the following run command. This will automatically 
deploy the module before the server is started. To streamline the process, add the following run configuration to your IDE to efficiently build, deploy and run the project.:

  ```bash
  mvn clean install openmrs-sdk:run -DserverId=myserver

* Once OpenMRS is up and running, you can upload the new module (imaging-1.0.0-SNAPSHOT.omod) using the 'Add or Upgrade Module' option in Manage Modules. 
Please note that the upload may take some time.
