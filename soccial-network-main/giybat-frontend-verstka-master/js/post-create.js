let postId = null;
let currentPost = null;

async function createPost() {

    const fileInput = document.getElementById('post_image_id');
    const file = fileInput.files[0];
    const titleValue = document.getElementById("post_title_id").value;
    const contentValue = document.getElementById("post_content_id").value;

    if (!file || !titleValue || !contentValue) {
        alert("Barcha maydonlarni to'ldiring!")
        return;
    }

    // image upload
    const imageId = await uploadImage();  // awaitni vazifasi uploadImage()ni ishga tushurib yakunlangandan keyin 18-qatordan keyingi kodlarga o'tib ketishini taminlaydi

    const body = {
        "title": titleValue,
        "content": contentValue,
        "photo": {
            "id": imageId
        }
    }

    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    return fetch('http://localhost:8080/api/v1/posts', {
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
            window.location.href = './profile-post-list.html';
            if (data.id) {
                return data.id;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            return null;
        });
}

// ------------ Image preview ------------
function previewImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function () {
        const imgContainer = document.getElementById('post_image_block');
        imgContainer.style.backgroundImage = 'url(' + reader.result + ')';
    };
    if (file) {
        reader.readAsDataURL(file);
    }
}

// ------------ Image upload ------------
async function uploadImage() {
    const fileInput = document.getElementById('post_image_id');
    const file = fileInput.files[0];
    if (file) {
        const formData = new FormData();
        formData.append('file', file);

        const jwt = localStorage.getItem('jwtToken');
        if (!jwt) {
            window.location.href = './login.html';
            return;
        }
        const lang = document.getElementById("current-lang").textContent;

        return fetch('http://localhost:8080/api/v1/attach/upload', {
            method: 'POST',
            headers: {
                'Accept-Language': lang,
                'Authorization': 'Bearer ' + jwt
            },
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                if (data.id) {
                    return data.id;
                }
            })
            .catch(error => {
                console.error('Error:', error);
                return null;
            });
    }
}

window.addEventListener("DOMContentLoaded", function () {
    const url_string = window.location.href;
    const url = new URL(url_string);
    const idParam = url.searchParams.get("id");

    if (idParam) {
        getPostById(idParam);
        document.getElementById("post_create_btn_group").classList.add("display-none");
        document.getElementById("post_update_btn_group").classList.remove("display-none");
        document.getElementById("post_page_title_id").textContent = "G'iybatni o'zgartirish";
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
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text().then(text => Promise.reject(text));
            }
        })
        .then(data => {
            console.log("Post ma'lumotlari yuklandi:", data);
            currentPost = data;

            // Sarlavha va kontentni to'ldirish
            const titleInput = document.getElementById("post_title_id");
            const contentArea = document.getElementById("post_content_id");

            if (titleInput) titleInput.value = data.title || "";
            if (contentArea) contentArea.value = data.content || "";

            // Rasm preview
            if (data.photo && data.photo.url) {
                const imgContainer = document.getElementById('post_image_block');
                if (imgContainer) {
                    imgContainer.style.backgroundImage = 'url(' + data.photo.url + ')';
                }
            }
        })
        .catch(error => {
            console.error('Postni yuklashda xatolik:', error);
        });
}

async function updatePost() {
    if (currentPost == null) {
        return;
    }

    const fileInput = document.getElementById('post_image_id');
    const file = fileInput.files[0];

    let imageId = null;
    if (file) {
        imageId = await uploadImage();
    } else {
        imageId = (currentPost.photo && currentPost.photo.id) ? currentPost.photo.id : null;
    }

    const titleValue = document.getElementById("post_title_id").value;
    const contentValue = document.getElementById("post_content_id").value;

    if (!imageId || !titleValue || !contentValue) {
        alert("Enter all inputs")
        return;
    }

    const body = {
        "title": titleValue,
        "content": contentValue,
        "photo": {
            "id": imageId
        }
    }

    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    return fetch('http://localhost:8080/api/v1/posts/' + currentPost.id, {
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
            alert("update boldiiii")
            window.location.href = './profile-post-list.html';
            if (data.id) {
                return data.id;
            }
        })
        .catch(error => {
            console.error('Error:', error);
            return null;
        });

}

function deletePost() {
    if (currentPost == null) {
        return;
    }
    if (!confirm("G'iybatni o'chirmoqchimisiz?")) {
        return;
    }
    const jwt = localStorage.getItem('jwtToken');
    if (!jwt) {
        window.location.href = './login.html';
        return;
    }
    const lang = document.getElementById("current-lang").textContent;

    return fetch('http://localhost:8080/api/v1/posts/' + currentPost.id, {
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
            alert("Deleted!")
            window.location.href = './profile-post-list.html';
        })
        .catch(error => {
            console.error('Error:', error);
            return null;
        });


}

