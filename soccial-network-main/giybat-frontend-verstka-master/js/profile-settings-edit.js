window.onload = function () {
    const userDetailJon = localStorage.getItem("userDetail");
    if (!userDetailJon) {
        return;
    }

    const userDetailObj = JSON.parse(userDetailJon);

    document.getElementById("profile_settings_name").value = userDetailObj.name;
    document.getElementById("profile_settings_username").value = userDetailObj.username;

    if (userDetailObj.photo){
        document.getElementById("profile_settings_photo").src = userDetailObj.photo.url;
    }
};

// name update
function profileDetailUpdate() {
    const name = document.getElementById("profile_settings_name").value
    const jwtToken = localStorage.getItem("jwtToken");
    const nameErrorSpan = document.getElementById("nameErrorSpan")
    if (!name ) {
        return;
    }
    if (!jwtToken) {
        window.location.href = "./login.html";
        return;
    }

    const body = {
        "name" : name
    }

    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/profile/detail",{
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': "Bearer " + jwtToken
        },
        body: JSON.stringify(body)
    })
        .then(response =>{
            if (response.ok){
                return response.json()
            }else {
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            alert(data.message)
            nameErrorSpan.style.display = "block";
            document.getElementById("nameErrorSpan").style.borderColor = "#ddd";
            document.getElementById("nameErrorSpan").style.color = "";
            // userDetail obyektini olish
            const userDetail = JSON.parse(localStorage.getItem("userDetail"));

            // Obyektdagi name maydonini yangilash
            userDetail.name = name;

            // Yangilangan obyektni localStorage'ga qayta yozish
            localStorage.setItem("userDetail", JSON.stringify(userDetail));

            const headerUserNameSpan = document.getElementById("header_user_name_id");
            headerUserNameSpan.textContent = name;

        })
        .catch(error =>{
            error.then(errorMessage =>{
                console.log(errorMessage.toString());
                nameErrorSpan.style.display = "block";
                document.getElementById("nameErrorSpan").style.color = "red";
                nameErrorSpan.textContent = errorMessage.toString();
            })
        })

}

// password update
function profilePasswordUpdate() {
    const currentPswd = document.getElementById("profile_settings_current_pswd").value
    const newPswd = document.getElementById("profile_settings_new_pswd").value
    const jwtToken = localStorage.getItem("jwtToken");
    const currentPasswordErrorSpan = document.getElementById("currentPasswordErrorSpan")

    if (!currentPswd || !newPswd) {
        alert("Enter all inputs")
        return;
    }
    if (!jwtToken) {
        window.location.href = "./login.html";
        return;
    }

    const body = {
        "currentPswd" : currentPswd,
        "newPswd" : newPswd
    }

    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/profile/password",{
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': "Bearer " + jwtToken
        },
        body: JSON.stringify(body)
    })
        .then(response =>{
            if (response.ok){
                return response.json()
            }else {
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            alert(data.message)
            document.getElementById("profile_settings_current_pswd").value = '';
            document.getElementById("profile_settings_new_pswd").value = '';

            currentPasswordErrorSpan.style.display = "none";
            document.getElementById("currentPasswordErrorSpan").style.borderColor = "#ddd";
            document.getElementById("currentPasswordErrorSpan").style.color = "";
        })
        .catch(error =>{
            error.then(errorMessage =>{
                console.log(errorMessage.toString());
                currentPasswordErrorSpan.style.display = "block";
                document.getElementById("currentPasswordErrorSpan").style.color = "red";
                currentPasswordErrorSpan.textContent = errorMessage.toString();
            })
        })

}

// username update
function profileUserNameChange() {

    const username = document.getElementById("profile_settings_username").value
    const jwtToken = localStorage.getItem("jwtToken");
    if (!username) {
        alert("Enter all inputs")
        return;
    }
    if (!jwtToken) {
        window.location.href = "./login.html";
        return;
    }
    const body = {
        "username" : username
    }

    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/profile/username",{
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': "Bearer " + jwtToken
        },
        body: JSON.stringify(body)
    })
        .then(response =>{
            if (response.ok){
                return response.json()
            }else {
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            document.getElementById("confirmModalResultId").textContent = data.message
            console.log("then---" + data.message)
            openModal()
        })
        .catch(error =>{
            error.then(errorMessage =>{
                console.log("catch---" + errorMessage.toString());
                alert(errorMessage)
            })
        })
}

function profileUserNameChangeConfirm() {
    const confirmCode = document.getElementById("profileUserNameChaneConfirmInputId").value
    const username = document.getElementById("profile_settings_username").value
    console.log(confirmCode)
    if (!confirmCode) {
        alert("Enter all inputs")
        return;
    }
    closeModal()
    const jwtToken = localStorage.getItem("jwtToken");
    if (!jwtToken) {
        window.location.href = "./login.html";
        return;
    }
    const body = {
        "code" : confirmCode
    }

    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/profile/username/confirm",{
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': "Bearer " + jwtToken
        },
        body: JSON.stringify(body)
    })
        .then(response =>{
            if (response.ok){
                return response.json()
            }else {
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            alert(data.message);
            console.log("then---" + data);
            // userDetail obyektini olish
            const userDetail = JSON.parse(localStorage.getItem("userDetail"));

            // Obyektdagi jwt maydonini yangilash
            userDetail.jwt = data.data;
            userDetail.username = document.getElementById("profile_settings_username").value;

            // Yangilangan obyektni localStorage'ga qayta yozish
            localStorage.setItem("userDetail", JSON.stringify(userDetail));
            localStorage.setItem("jwtToken", data.data);
        })
        .catch(error =>{
            closeModal()
            error.then(errorMessage =>{
                console.log("catch---" + errorMessage.toString());
                alert(errorMessage)
            })
        })

}

//------------ Change username confirm modal start ------------
const modal = document.getElementById('simpleModalId');

function openModal() {
    modal.style.display = 'block';
}

function closeModal() {
    modal.style.display = "none";
}

window.onclick = (event) => {
    if (event.target === modal) {
        modal.style.display = 'none';
    }
};

//------------ Change username confirm modal end ------------

function previewImage(event){
    console.log(event);
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function () {
        const img = document.getElementById('profile_settings_photo');
        img.src = reader.result;
    };

    if (file) {
        reader.readAsDataURL(file);
        document.getElementById('profile_settings_upload_img_btn_id').style.display = 'inline-block';
    }
}

function uploadImage(){
    const fileInput = document.getElementById('imageUpload');
    const file = fileInput.files[0];
    if (file){
        const formData = new FormData();
        formData.append('file', file);

        const jwt = localStorage.getItem('jwtToken');
        if (!jwt) {
            window.location.href = './login.html';
            return;
        }
        const lang = document.getElementById("current-lang").textContent;

        fetch('http://localhost:8080/api/v1/attach/upload', {
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
                console.log('Success:', data);
                if(data.id){
                    updateProfileImage(data.id); // profile update image
                    const userDetailJon = localStorage.getItem("userDetail");
                    const userDetail = JSON.parse(userDetailJon);
                    userDetail.photo = {};
                    userDetail.photo.id = data.id;
                    userDetail.photo.url = data.url;
                    localStorage.setItem("userDetail", JSON.stringify(userDetail));

                    document.getElementById("header_user_image_id").src =data.url;
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
}

function updateProfileImage(photoId){
    const jwtToken = localStorage.getItem("jwtToken");
    const nameErrorSpan = document.getElementById("nameErrorSpan")
    if (!photoId ) {
        return;
    }
    if (!jwtToken) {
        window.location.href = "./login.html";
        return;
    }

    const body = {
        "photoId" : photoId
    }

    const lang = document.getElementById("current-lang").textContent

    fetch("http://localhost:8080/api/v1/profile/photo",{
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            'Accept-Language': lang,
            'Authorization': "Bearer " + jwtToken
        },
        body: JSON.stringify(body)
    })
        .then(response =>{
            if (response.ok){
                return response.json()
            }else {
                return Promise.reject(response.text());
            }
        })
        .then(data => {
            document.getElementById('profile_settings_upload_img_btn_id').style.display = 'none';
            alert(data.message)
        })
        .catch(error =>{
            error.then(errorMessage =>{
                console.log(errorMessage.toString());
            })
        })
}