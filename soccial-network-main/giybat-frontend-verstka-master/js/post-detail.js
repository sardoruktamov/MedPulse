window.addEventListener("DOMContentLoaded", function () {
    console.log("Post-detail.js v2.0 yuklandi!");
    var url_string = window.location.href;
    var url = new URL(url_string);
    var id = url.searchParams.get("id");

    if (id) {
        getPostById(id);
        getPostList(id);
    } else {
        loadFakePost();
    }

    loadFakeComments();

    const sendBtn = document.getElementById("sendCommentBtn");
    if (sendBtn) {
        sendBtn.addEventListener("click", function () {
            const input = document.getElementById("commentInput");
            if (input.value.trim()) {
                addNewComment("Siz (Mening Profilim)", "./images/photo.png", input.value.trim());
                input.value = "";
            }
        });
    }
});

function getPostById(idParam) {
    const langElement = document.getElementById("current-lang");
    const lang = langElement ? langElement.textContent : "UZ";

    return fetch('http://localhost:8080/api/v1/posts/public/' + idParam, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
        }
    })
        .then(response => response.ok ? response.json() : Promise.reject(response.text()))
        .then(data => {
            const image = document.getElementById("postDetailImgId");
            if (image) {
                image.src = (data.photo && data.photo.url) ? data.photo.url : './images/default-img.png';
            }
            const dateEl = document.getElementById("post-detail-dateId");
            if (dateEl) dateEl.textContent = formatDate(data.createdDate);
            const titleEl = document.getElementById("post-detail-titleId");
            if (titleEl) titleEl.textContent = data.title;
            const contentEl = document.getElementById("post-detail-contentId");
            if (contentEl) contentEl.innerHTML = data.content;
        })
        .catch(error => console.error('Error fetching post:', error));
}

function getPostList(exceptId) {
    const langElement = document.getElementById("current-lang");
    const lang = langElement ? langElement.textContent : "UZ";

    fetch('http://localhost:8080/api/v1/posts/public/similar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
        },
        body: JSON.stringify({ "exceptId": exceptId })
    })
        .then(response => response.ok ? response.json() : Promise.reject())
        .then(data => {
            if (data && data.length > 0) showPostList(data);
        })
        .catch(error => console.error('Error fetching similar posts:', error));
}

function formatDate(isoDateString) {
    if (!isoDateString) return "";
    const date = new Date(isoDateString);
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${day}.${month}.${year} ${hours}:${minutes}`;
}

function showPostList(postList) {
    const parent = document.getElementById("similar-post-container-id");
    if (!parent) return;
    parent.innerHTML = '';
    postList.forEach(postItem => {
        const div = document.createElement("div");
        div.classList.add("post_box");
        const a = document.createElement("a");
        a.href = "./post-detail.html?id=" + postItem.id;
        const imageDiv = document.createElement("div");
        imageDiv.classList.add("post_img__box");
        const img = document.createElement("img");
        img.src = (postItem.photo && postItem.photo.url) ? postItem.photo.url : './images/default-img.png';
        img.classList.add('post_img');
        imageDiv.appendChild(img);
        const title = document.createElement("h3");
        title.classList.add("post_title");
        title.textContent = postItem.title;
        const createdDate = document.createElement("p");
        createdDate.classList.add("post_text");
        createdDate.textContent = formatDate(postItem.createdDate);
        a.appendChild(imageDiv);
        a.appendChild(title);
        a.appendChild(createdDate);
        div.appendChild(a);
        parent.appendChild(div);
    });
}

function loadFakePost() {
    const img = document.getElementById("postDetailImgId");
    if (img) img.src = './images/book1.png';
    const date = document.getElementById("post-detail-dateId");
    if (date) date.textContent = "08.03.2026 18:20";
    const title = document.getElementById("post-detail-titleId");
    if (title) title.textContent = "Sun'iy intellektning tibbiyotdagi kelajagi";
    const content = document.getElementById("post-detail-contentId");
    if (content) content.innerHTML = `<p>Sun'iy intellekt (SI) haqida fake ma'lumot...</p>`;
}

function loadFakeComments() {
    // Eng birinchi izohlar tepaga turishi uchun tartib: eskidan yangiga
    const comments = [
        { name: "Nodir", avatar: "./images/photo.png", text: "Backend qachon tayyor?", date: "07.03.2026, 17:10" },
        { name: "Sardor", avatar: "./images/photo.png", text: "Juda dolzarb mavzu!", date: "Bugun, 10:15" },
        { name: "Anvaraka", avatar: "./images/photo.png", text: "Birinchi commentim!", date: "Bugun, 09:15" }
    ];

    const container = document.getElementById("commentContainer");
    if (container) {
        container.innerHTML = "";
        comments.forEach(c => renderComment(c.name, c.avatar, c.text, c.date));
    } else {
        console.warn("commentContainer topilmadi!");
    }
}

function renderComment(name, avatar, text, date) {
    const container = document.getElementById("commentContainer");
    if (!container) return;
    const item = document.createElement("div");
    item.classList.add("comment_item");
    item.innerHTML = `
        <a href="profile-settings.html" class="comment_avatar_link">
            <img src="${avatar}" alt="${name}" class="comment_avatar">
        </a>
        <div class="comment_content">
            <a href="profile-settings.html" class="comment_author_name">${name}</a>
            <p class="comment_text">${text}</p>
            <span class="comment_date">${date}</span>
        </div>
    `;
    container.appendChild(item);
}

function addNewComment(name, avatar, text) {
    const container = document.getElementById("commentContainer");
    if (!container) return;
    const item = document.createElement("div");
    item.classList.add("comment_item");
    item.innerHTML = `
        <a href="profile-settings.html" class="comment_avatar_link">
            <img src="${avatar}" alt="${name}" class="comment_avatar">
        </a>
        <div class="comment_content">
            <a href="profile-settings.html" class="comment_author_name">${name}</a>
            <p class="comment_text">${text}</p>
            <span class="comment_date">Hozirgina</span>
        </div>
    `;
    container.appendChild(item); // Yangi izoh pastga qo'shiladi
}