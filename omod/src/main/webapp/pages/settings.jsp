<%@ include file="/WEB-INF/template/include.jsp"%> <%@ include
file="/WEB-INF/template/header.jsp"%>
<html>
  <head>
    <title>Orthanc Server Configuration</title>
    <style>
      body {
        font-family: Arial, sans-serif;
      }
      .form-container {
        width: 50%;
        margin: 0 auto;
        padding: 20px;
        border: 1px solid #ccc;
        border-radius: 5px;
        box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      }
      .form-container h2 {
        text-align: center;
      }
      .form-group {
        margin-bottom: 15px;
      }
      .form-group label {
        display: block;
        font-weight: bold;
        margin-bottom: 5px;
      }
      .form-group input {
        width: 100%;
        padding: 8;
      }
    </style>
  </head>
  <body>
    <div class="form-container">
      <h2>Orthanc Server Configuration</h2>
      <form action="module/imaging/imagingLink.form" method="post">
        <div class="form-group">
          <label for="orthancServerUrl">Orthanc Server URL:</label>
          <input
            type="text"
            id="orthancServerUrl"
            name="orthancServerUrl"
            required
          />
        </div>
        <div class="form-group">
          <label for="orthancServerPort">Orthanc Server Port:</label>
          <input
            type="text"
            id="orthancServerPort"
            name="orthancServerPort"
            required
          />
        </div>
        <div class="form-group">
          <label for="orthancServerUsername">Orthanc Server Username:</label>
          <input
            type="text"
            id="orthancServerUsername"
            name="orthancServerUsername"
            required
          />
        </div>
        <div class="form-group">
          <label for="orthancServerPassword">Orthanc Server Password:</label>
          <input
            type="password"
            id="orthancServerPassword"
            name="orthancServerPassword"
            required
          />
        </div>
        <button type="submit">Save Settings</button>
      </form>
    </div>
  </body>
</html>
