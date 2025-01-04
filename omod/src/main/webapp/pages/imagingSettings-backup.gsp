<% ui.decorateWith("appui", "standardEmrPage") %>

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
        <% try { %>
            ${status}
         <% } catch (Exception e) { }%>
      <form method="post">
        <div class="form-group">
          <label for="orthancBaseUrl">Orthanc server base URL:</label>
          <input
            type="text"
            id="orthancBaseUrl"
            name="orthancBaseUrl"
            value=${orthancBaseUrl ?:""}
            required
          />
        </div>
        <div class="form-group">
          <label for="orthancUsername">Orthanc server username:</label>
          <input
            type="text"
            id="orthancUsername"
            name="orthancUsername"
            value=${orthancUsername ?:""}
            required
          />
        </div>
        <div class="form-group">
          <label for="orthancPassword">Orthanc server password:</label>
          <input
            type="password"
            id="orthancPassword"
            name="orthancPassword"
            required
          />
        </div>
        <br />
        <input type="submit" value="<openmrs:message code="orthancconfigure.save"/>" name="save">
      </form>
    </div>
  </body>
</html>
