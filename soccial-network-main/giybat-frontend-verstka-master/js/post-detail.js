window.addEventListener("DOMContentLoaded", function () {
    var url_string = window.location.href; // www.test.com?id=dasdasd
    var url = new URL(url_string);
    var id = url.searchParams.get("id");
    if (id) {
        getPostById(id);
        getPostList(id);
    }
});

function getPostById(idParam) {
    const lang = document.getElementById("current-lang").textContent;

    return fetch('http://localhost:8080/api/v1/posts/public/'+idParam, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
        }
    })
        .then(response => {
            if (response.ok) {
                return response.json();
            }else {
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            // console.log(data)
            //photo
            const image = document.getElementById("postDetailImgId");
            if (data.photo && data.photo.url){
                image.src = data.photo.url;
            }else {
                image.src = './images/post-default-img.jpg';
            }
            document.getElementById("post-detail-dateId").textContent = formatDate(data.createdDate);
            document.getElementById("post-detail-titleId").textContent = data.title;
            document.getElementById("post-detail-contentId").innerHTML = data.content;
        })
        .catch(error => {
            console.error('Error:', error);
            return null;
        });

}

function getPostList(exceptId) {
    const lang = document.getElementById("current-lang").textContent;
    const body = {
        "exceptId": exceptId
    }
    fetch('http://localhost:8080/api/v1/posts/public/similar', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang
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
            console.log('Success:', data);
            if (data && data.length > 0){
                showPostList(data);
                console.log('Success:', data.content);
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
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

// similar post list
function showPostList(postList) {
    const parent = document.getElementById("similar-post-container-id")
    parent.innerHTML = '';
    postList.forEach(postItem => {
        //
        const div = document.createElement("div");
        div.classList.add("post_box");
        // button
        const a = document.createElement("a");
        // editButton.classList.add("profile_tab_btn");
        a.href = "./post-detail.html?id=" + postItem.id;

        // image_div
        const imageDiv = document.createElement("div");
        imageDiv.classList.add("post_img__box");
        // image
        const img = document.createElement("img");
        if (postItem.photo && postItem.photo.id) {
            img.src = postItem.photo.url;
        } else {
            img.src = './images/post-default-img.jpg';
        }
        img.classList.add('post_img');
        imageDiv.appendChild(img);

        // title
        const title = document.createElement("h3");
        title.classList.add("post_title");
        title.textContent = postItem.title

        // created_date
        const createdDate = document.createElement("p");
        createdDate.classList.add("post_text");
        createdDate.textContent = formatDate(postItem.createdDate);

        // add elements to a
        a.appendChild(imageDiv)
        a.appendChild(title);
        a.appendChild(createdDate);
        // add elements to main div
        div.appendChild(a);
        //
        parent.appendChild(div);
    });

}