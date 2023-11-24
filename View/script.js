const url = "http://localhost:8080/tasks/user/1";

function hideLoader() {
    document.getElementById("loading").style.display = "none";
}

function show(tasks) {
    let tab = `<thead>` +
        `<th scope="col">#</th>` +
        `<th scope="col">Description</th>` +
        `<th scope="col">Username</th>` +
        `<th scope="col">User Id</th>` +
        `</thead>`;

    for (let task of tasks) {
        tab += `<tr>` +
            `<td scope="row">${task.id}</td>` +
            `<td scope="row">${task.description}</td>` +
            `<td scope="row">${task.user.username}</td>` +
            `<td scope="row">${task.user.id}</td>` +
            `</tr>`;
    }

    document.getElementById("tasks").innerHTML = tab;
}


async function getAPI(url) {
    try {
        const response = await fetch(url, { method: "GET" });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }

        const data = await response.json();
        console.log(data);
        hideLoader();
        show(data);
    } catch (error) {
        console.error("Error fetching data:", error);
    }
}

getAPI(url);
