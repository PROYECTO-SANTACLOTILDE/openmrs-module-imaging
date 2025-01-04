<% ui.decorateWith("appui", "standardEmrPage", [ title: ui.message("imaging.app.orthancconfiguration.title") ])
   ui.includeCss("imaging", "orthancConfiguration.css");
%>

<h2>${ ui.message("imaging.app.orthancconfiguration.heading")}</h2>
</br>
<body>
    <script>
        function togglePopupAdd() {
            const overlay = document.getElementById('popupOverlayAdd');
            overlay.classList.toggle('show');
        }
    </script>
    <div>
        <button class="btn-open-popup" onclick="togglePopupAdd()">Add new configuration</button>
    </div>
    <table class="table table-sm table-responsive-sm table-responsive-md table-responsive-lg table-responsive-xl">
        <thead>
           <tr>
                <th>${ ui.message("imaging.app.orthancid.label")}</th>
                <th>${ ui.message("imaging.app.url.label")}</th>
                <th>${ ui.message("imaging.app.username.label")}</th>
            </tr>
        </thead>
        <tbody>
            <% if (orthancConfigurations.size() == 0) { %>
                <tr>
                    <td colspan="3">${ui.message("imaging.app.none")}</td>
                </tr>
            <% } %>
            <% orthancConfigurations.each { orthancConfiguration -> %>
                <tr>
                    <td>${ui.format(orthancConfiguration.orthancId)}</td>
                    <td>${ui.format(orthancConfiguration.orthancBaseUrl)}</td>
                    <td>${ui.format(orthancConfiguration.orthancUsername)}</td>
                </tr>
            <% } %>
        </tbody>
    </table>
    <div id="popupOverlayAdd" class="overlay-container">
        <div class="popup-box">
            <h2 style="color: green;">Edit Orthanc configuration</h2>
            <form class="form-container" action="/openmrs/module/imaging/storeConfiguration.form" method="post">
                <label class="form-label" for="url">${ ui.message("imaging.app.url.label")}</label>
                <input class="form-input" type="text" placeholder="Orthanc URL" id="url" name="url" required>

                <label class="form-label" for="username">${ ui.message("imaging.app.username.label")}</label>
                <input class="form-input" type="text" placeholder="Orthanc user name" id="username" name="username" required>

                <label class="form-label" for="password">${ ui.message("imaging.app.password.label")}</label>
                <input class="form-input" type="password" placeholder="Orthanc password" id="password" name="password" required>

                <button class="btn-submit" type="submit">Submit</button>
            </form>
            <button class="btn-close-popup" onclick="togglePopupAdd()">Cancel</button>
        </div>
    </div>
</body>
</html>
