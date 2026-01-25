// header language dropdown
function toggleLanguageDropdown() {
    const dropdownContent = document.getElementById("dropdown-content");
    const dropdownToggle = document.querySelector(".dropdown_toggle");

    dropdownContent.classList.toggle("show");
    dropdownToggle.classList.toggle("active"); // Добавляем/удаляем класс для вращения стрелки
}

// Set language function
function setSelectedLanguage(lang) {
    document.getElementById("current-lang").textContent = lang;
    localStorage.setItem("current-lang",lang);
    toggleLanguageDropdown(); // Закрыть dropdown после выбора
}

// Close language dropdown when click outside
window.onclick = function (event) {
    if (!event.target.closest(".dropdown_toggle")) {
        const dropdownContent = document.getElementById("dropdown-content");
        const dropdownToggle = document.querySelector(".dropdown_toggle");

        if (dropdownContent.classList.contains("show")) {
            dropdownContent.classList.remove("show");
            dropdownToggle.classList.remove("active"); // Убираем класс, когда меню закрывается
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {
    const toggleButton = document.querySelector(".header_entrance__toggle");
    const menu = document.querySelector(".header_entrance__menu");

    toggleButton.addEventListener("click", () => {
        menu.classList.toggle("header_entrance__show");
    });

    // close menu if it is clicked outside
    document.addEventListener("click", (e) => {
        if (!toggleButton.contains(e.target) && !menu.contains(e.target)) {
            menu.classList.remove("header_entrance__show");
        }
    });

    // select language
    let currentLang = localStorage.getItem('current-lang');
    if (!currentLang){
        currentLang = "UZ";
    }
    localStorage.setItem("current-lang", currentLang)
    if (currentLang){
        document.getElementById("current-lang").textContent = currentLang;
    }

    //Show profile menu on header
    const userDetailStr = localStorage.getItem("userDetail");
    if (userDetailStr === null || userDetailStr === undefined || userDetailStr === '') {
        // window.location.href = "./404.html"
        return;
    }
    const userDetail = JSON.parse(userDetailStr);
    const userName = userDetail.name;
    const userImg = userDetail.photo.url;

    const loginBtn = document.getElementById("header_btn");
    loginBtn.style.display = "none";
    const menuUserDetailWrapper = document.getElementById("header_entrance");
    menuUserDetailWrapper.style.display = "block";

    const headerUserNameSpan = document.getElementById("header_user_name_id");
    headerUserNameSpan.textContent = userName;

    if (userDetail.photo){
        const headerUserImage = document.getElementById("header_user_image_id");
        headerUserImage.src = userImg;
    }

    // search input
    const searchBtn = document.getElementById("header-search-buttonId");
    if (searchBtn) {
        searchBtn.addEventListener("click", (e) => {
            const query = document.getElementById("header-search-inputId").value;
            if (query) {
                window.location.href = "./search-result-page.html?query=" + query;
            }
        });
    }

    const searchInput = document.getElementById("header-search-inputId")
    if (searchInput){
        searchInput.addEventListener("keypress",(e) => {
            if (e.key === "Enter"){
                e.preventDefault()
                if (searchInput.value) {
                    window.location.href = "./search-result-page.html?query=" + searchInput.value;
                }
            }
        });
    }

});

// logout
function logout() {
    const loginBtn = document.getElementById("header_btn");
    loginBtn.style.display = "block";

    const menuUserDetailWrapper = document.getElementById("header_entrance");
    menuUserDetailWrapper.style.display = "none";

    localStorage.removeItem("userDetail")
    localStorage.removeItem("jwtToken");

    window.location.href = "./index.html";
}
