import AppConfig from './AppConfig.js';

window.addEventListener("DOMContentLoaded", function () {
    getPostList();
});
let currentPage = 1;
let pageSize = 6;

function getPostList() {
    const jwt = localStorage.getItem('jwtToken');

    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    let postData = document.getElementById("admin_post_list_post_input_id").value;
    let profileData = document.getElementById("admin_post_list_profile_input_id").value;

    if (postData.length === 0 || postData.trim().length === 0){
        postData = null;
    }
    if (profileData.length === 0 || profileData.trim().length === 0){
        profileData = null;
    }
    const body = {
        "postQuery": postData,
        "profileQuery": profileData
    }
    fetch(AppConfig.API + "/posts/admin-post-list/filter?page=" + currentPage + "&size=" + pageSize, {
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
            showPostList(data.content);
            showPagination(data.totalElements);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function showPostList(postList) {
    const parent = document.getElementById("admin_post_list_table_id")
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

        //td title
        const  tdTitle = document.createElement("td");
        tdTitle.classList.add("td");
        tdTitle.innerHTML = postItem.title;

        //td CareatedDate
        const  tdDate = document.createElement("td");
        tdDate.classList.add("td");
        tdDate.innerHTML = formatDate(postItem.createdDate);

        //td profile
        const  tdUsername = document.createElement("td");
        tdUsername.classList.add("td");
        tdUsername.innerHTML = postItem.profile.name +"<br>"+ postItem.profile.username;

        //td delete
        const  tdAction = document.createElement("td");
        tdAction.classList.add("td", "d-flex");

            //td info
            const imageinfo = document.createElement("img");
            imageinfo.classList.add("table_basket", "hover-pointer");
            imageinfo.src = "./images/info.png";
            imageinfo.addEventListener("click", () => {
                // window.location.href = "./post-detail.html?id=" + postItem.id
                window.open("./post-detail.html?id=" + postItem.id)
            })

            // image delete
            const imageDet = document.createElement("img");
            imageDet.classList.add("table_basket", "hover-pointer");
            imageDet.src = "./images/basket.svg";
            imageDet.addEventListener("click", () => {deletePost(postItem.id)})

        tdAction.appendChild(imageinfo);
        tdAction.appendChild(imageDet);


        // hamma elementlarni div (post_box) ga qo‘shamiz
        tr.appendChild(tdnumber);
        tr.appendChild(tdImg);
        tr.appendChild(tdTitle);
        tr.appendChild(tdDate);
        tr.appendChild(tdUsername);
        // tr.appendChild(tdStatus);
        tr.appendChild(tdAction);


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

    return `${day}.${month}.${year} ${hours}:${minutes}`;
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
            getPostList();
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
            getPostList();
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
                getPostList();
            }
        }
    }


    btnWrapper.appendChild(btn);
    pageNumberWrapper.appendChild(btnWrapper);
}

function deletePost(id){
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    if (!confirm("Postni o'chirmoqchimisiz?")){
        return;
    }

    fetch(AppConfig.API + "/posts/" + id, {
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
            showPopup(data.message);
            getPostList()
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

document.addEventListener("DOMContentLoaded", () =>{
    document.getElementById("admin-post-search-btn-id").addEventListener("click", getPostList);
})
// window.getPostLis = getPostList()
