<table>
    <thead>
        <tr>
            <th>Study ID</th>
            <th>Description</th>
            <th>Date</th>
            <th>Actions</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="study" items="${studies}">
            <tr>
                <td>${study.studyInstanceUID}</td>
                <td>${study.studyDescription}</td>
                <td>${study.studyDate}</td>
                <td><a href="/module/imaging/study/${study.studyInstanceUID}">View</a></td>
            </tr>
        </c:forEach>
    </tbody>
</table>

