window.addEventListener("DOMContentLoaded", function () {
    getProfileList();
});
let currentPage = 1;
let pageSize = 10;

function getProfileList() {
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;
    const body = {
        "query": null
    }

    fetch('http://localhost:8080/api/v1/profile/filter?page=' + currentPage + "&size=" + pageSize, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            showProfileList(data.content);
            console.log(data)
            showPagination(data.totalElements);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function showProfileList(postList) {
    const parent = document.getElementById("profile_list_container_id")
    parent.innerHTML = '';
    postList.forEach((postItem, count) => {
        // parent div
        const tr = document.createElement("tr");
        tr.classList.add("tr");

        //td number
        const  tdnumber = document.createElement("td");
        tdnumber.classList.add("td");
        tdnumber.innerHTML = (currentPage - 1) * pageSize + count + 1;

        //td image
        const  tdImg = document.createElement("td");
        tdImg.classList.add("td");
            // image div
            const image = document.createElement("img");
            image.classList.add("table_photo");
            if (postItem.photo && postItem.photo.url) {
                // Agar photo obyekti VA uning ichida url bo'lsa
                image.src = postItem.photo.url;
            } else {
                // Agar rasm bo'lmasa, default (zaxira) rasmni qo'yish
                image.src = "./images/default-user.png"; // yoki "./images/book1.png"
            }
        tdImg.appendChild(image);

        //td name
        const  tdname = document.createElement("td");
        tdname.classList.add("td");
        tdname.innerHTML = postItem.name;

        //td Username
        const  tdUsername = document.createElement("td");
        tdUsername.classList.add("td");
        tdUsername.innerHTML = postItem.username;

        //td CareatedDate
        const  tdDate = document.createElement("td");
        tdDate.classList.add("td");
        tdDate.innerHTML = formatDate(postItem.createdDate);

        //td PostCount
        const  tdPostCount = document.createElement("td");
        tdPostCount.classList.add("td");
        tdPostCount.innerHTML = postItem.postCount;

        //td Roles
        const  tdRoles = document.createElement("td");
        tdRoles.classList.add("td");
        if (postItem.roleList){
            tdRoles.innerHTML = postItem.roleList.join("<br>");
        }

        //td Status
        const  tdStatus = document.createElement("td");
        tdStatus.classList.add("td");
            const  statusButton = document.createElement("button");
            statusButton.classList.add("table_btn");
            if (postItem.status === "ACTIVE") {
                statusButton.classList.add("table_btn_active");
                statusButton.innerHTML = "ACTIVE";
                statusButton.addEventListener("click", () => {changeStatus(postItem.id, "BLOCK")})
            }else if (postItem.status === "BLOCK") {
                statusButton.classList.add("table_btn_block");
                statusButton.innerHTML = "BLOCK";
                statusButton.addEventListener("click", () => {changeStatus(postItem.id, "ACTIVE")})
            }else {
                statusButton.classList.add("table_btn_in_registration");
                statusButton.innerHTML = "IN_REGISTRATION";
            }

        tdStatus.appendChild(statusButton);

        //td delete
        const  tdDelete = document.createElement("td");
        tdDelete.classList.add("td");
        // image delete
        const imageDet = document.createElement("img");
        imageDet.classList.add("table_basket", "hover-pointer");
        imageDet.src = "./images/basket.svg";
        imageDet.addEventListener("click", () => {deleteUser(postItem.id)})
        tdDelete.appendChild(imageDet);

        // hamma elementlarni div (post_box) ga qo‘shamiz
        tr.appendChild(tdnumber);
        tr.appendChild(tdImg);
        tr.appendChild(tdname);
        tr.appendChild(tdUsername);
        tr.appendChild(tdDate);
        tr.appendChild(tdPostCount);
        tr.appendChild(tdRoles);
        tr.appendChild(tdStatus);
        tr.appendChild(tdDelete);


        parent.appendChild(tr);
    })
}

// sanani formatlash
function formatDate(isoDateString) {
    const date = new Date(isoDateString);

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 0-based
    const year = date.getFullYear();

    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${day}.${month}.${year}   ${hours}:${minutes}`;
}

function showPagination(totalElements) {
    let totalPageCount = Math.ceil(totalElements / pageSize);

    const paginationWrapper = document.getElementById("paginationWrapperId");
    paginationWrapper.innerHTML = '';

    // previous button
    const prevDiv = document.createElement("div");
    prevDiv.classList.add("pagination_btn__box");

    const prevButton = document.createElement("button");
    prevButton.classList.add("pagination_btn", "pagination-back");
    prevButton.textContent = "Oldinga";
    prevButton.onclick = () => {
        if (currentPage > 1) {
            currentPage--;
            getProfileList();
        }
    }
    prevDiv.appendChild(prevButton);
    paginationWrapper.appendChild(prevDiv);

    // page numbers
    const pageNumberWrapper = document.createElement("div");
    pageNumberWrapper.classList.add("pagination_block");

    let startPage = Math.max(1, currentPage - 2);
    let endPage = Math.min(totalPageCount, currentPage + 2);

    if (startPage > 1) { // show first page
        addBtn(1, pageNumberWrapper, false, false)
        if (startPage > 2) { // add ...
            addBtn("...", pageNumberWrapper, false, true)
        }
    }

    for (let i = startPage; i <= endPage; i++) {
        addBtn(i, pageNumberWrapper, i === currentPage)
    }

    if (endPage < totalPageCount) { // show last page
        if (endPage < totalPageCount - 1) { // add ...
            addBtn("...", pageNumberWrapper, false, true)
        }
        addBtn(totalPageCount, pageNumberWrapper, false, false)
    }


    paginationWrapper.appendChild(pageNumberWrapper);

    // next button
    const nextDiv = document.createElement("div");
    nextDiv.classList.add("pagination_btn__box");
    const nextButton = document.createElement("button");
    nextButton.classList.add("pagination_btn", "pagination-forward");
    nextButton.textContent = "Keyingi";
    nextButton.onclick = () => {
        if (currentPage < totalPageCount) {
            currentPage++;
            getProfileList();
        }
    }

    nextDiv.appendChild(nextButton);
    paginationWrapper.appendChild(nextDiv);
}

function addBtn(btnText, pageNumberWrapper, isSelected, isDots) {
    const btnWrapper = document.createElement("div");
    btnWrapper.classList.add("pagination_btn__box");
    const btn = document.createElement("button");
    btn.textContent = btnText;
    if (isDots) {
        btn.classList.add("pagination_btn_dots");
    } else {
        if (isSelected) {
            btn.classList.add("pagination_active");
        } else {
            btn.classList.add("pagination_btn");

            btn.onclick = () => {
                currentPage = btnText;
                getProfileList();
            }
        }
    }


    btnWrapper.appendChild(btn);
    pageNumberWrapper.appendChild(btnWrapper);
}

function changeStatus(id, status){
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    if (!confirm("Statusni o'zgartirmoqchimisiz?")){
        return;
    }

    const body = {
        "status": status
    }

    fetch('http://localhost:8080/api/v1/profile/status/' + id, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        },
        body: JSON.stringify(body)
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            showPopup(data.message)
            getProfileList()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function deleteUser(id){
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    if (!confirm("Foydalanuvchini o'chirmoqchimisiz?")){
        return;
    }

    fetch('http://localhost:8080/api/v1/profile/' + id, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': 'Bearer ' + jwt
        }
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            alert(data.message)
            getProfileList()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}