window.onload = function () {
    getPostList();
};
window.addEventListener("DOMContentLoaded", function () {
    getPostList();
});

let currentPage = 1;

function getPostList() {
    const jwt = localStorage.getItem('jwtToken');

    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;
    
    let size = 6;
    fetch('http://localhost:8080/api/v1/posts/profile?page=' + currentPage + "&size=" + size, {
        method: 'GET',
        headers: {
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
            showPostList(data.content);
            showPagination(data.totalElements, size);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

function showPostList(postList) {
    const parent = document.getElementById("profile_post_container_id")
    parent.innerHTML = '';
    postList.forEach(postItem => {
        // parent div
        const div = document.createElement("div");
        div.classList.add("position-relative","post_box");
        //button
        const  editButton = document.createElement("a");
        editButton.classList.add("profile_tab_btn");
        editButton.href = "./post-create.html?id=" + postItem.id

        // image div
        const imageDiv = document.createElement("div");
        imageDiv.classList.add("post_img__box");
            // img
            const img = document.createElement("img");
            if(postItem.photo && !postItem.photo.id){
                img.src = "./images/book1.png";
            }else {
                img.src = postItem.photo.url;
            }
            img.alt = "Posts";
            img.classList.add("post_img");

        imageDiv.appendChild(img); // rasmni rasm konteyneriga qo‘shish

        // <h3> title
        const h3 = document.createElement("h3");
        h3.classList.add("post_title");
        h3.textContent = postItem.title;

        // <p> sana
        const p = document.createElement("p");
        p.classList.add("post_text");
        p.textContent = formatDate(postItem.createdDate);

        // hamma elementlarni div (post_box) ga qo‘shamiz
        div.appendChild(editButton);
        div.appendChild(imageDiv);
        div.appendChild(h3);
        div.appendChild(p);


        parent.appendChild(div);
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

function showPagination(totalElements, size) {
    let totalPageCount = Math.ceil(totalElements / size);

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




