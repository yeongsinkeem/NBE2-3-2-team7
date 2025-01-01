let filterBox;
let placeBox;
let eventBox;

window.addEventListener('DOMContentLoaded', () => {
    filterBox = document.getElementById('main-filter-box');
    placeBox = document.getElementById('main-filter-place');
    eventBox = document.getElementById('main-filter-event');
    slideImage();

    createPriceSlider();
    createPlaceSlider();
    createPickCalendar();

    // 인증 상태에 따른 UI 업데이트
    updateAuthUI();
});

function slideImage() {
    const slides = document.getElementById('slide-container');
    const slideItems = document.querySelectorAll('#slide-container > div');
    const indicators = document.querySelectorAll('.indicator');

    let interval_3;

    let index = 0;

    const totalSlides = slideItems.length;

    const prevButton = document.getElementById('left-slide-arr');
    const nextButton = document.getElementById('right-slide-arr');

    prevButton.addEventListener('click', () => {
        index = (index - 1 + totalSlides) % totalSlides;
        moveSlide(slides, index);
        resetInterval();
    });

    nextButton.addEventListener('click', () => {
        index = (index + 1) % totalSlides;
        moveSlide(slides, index);
        resetInterval();
    });

    indicators.forEach((indicator, i) => {
        indicator.addEventListener('click', () => {
            index = i;
            moveSlide(slides, index);
            resetInterval();
        });
    });

    resetInterval();

    function resetInterval() {
        if (interval_3) clearInterval(interval_3);

        interval_3 = setInterval(() => {
            index = (index + 1) % totalSlides;
            moveSlide(slides, index);
        }, 3000);
    }

    function moveSlide(slides, index) {
        slides.style.transform = `translateX(-${index * 100}%)`;
    }
}

let placeSt = true;
let eventSt = false;

function findPlace() {
    let insert = `<div class="flex space-x-4">
                        <div class="bg-white px-4 py-2 flex-grow rounded-md border border-gray-300">
                            <div class="mb-2 flex justify-between space-x-2">
                                <h3 class="font-bold">면적</h3>
                                <label id="place-range" class="text-center"></label>
                            </div>
                            <div id="place-slider" class="w-full"></div>
                        </div>
                        <div class="bg-white px-4 py-2 flex-shrink-0 border border-gray-300 rounded-md">
                            <label for="location" class="font-bold">지역</label>
                            <select id="location" class="w-full px-1">
                                <option value="all">지역</option>
                                <option value="서울">서울</option>
                                <option value="부산">부산</option>
                                <option value="대구">대구</option>
                                <option value="인천">인천</option>
                                <option value="광주">광주</option>
                                <option value="대전">대전</option>
                                <option value="울산">울산</option>
                                <option value="세종">세종</option>
                                <option value="경기">경기</option>
                                <option value="충북">충북</option>
                                <option value="충남">충남</option>
                                <option value="전북">전북</option>
                                <option value="전남">전남</option>
                                <option value="경북">경북</option>
                                <option value="경남">경남</option>
                                <option value="강원">강원</option>
                                <option value="제주">제주</option>
                            </select>
                        </div>
                    </div>
                    <div class="bg-white px-4 py-2 rounded-md border border-gray-300 min-w-72">
                        <div class="mb-2 flex justify-between">
                            <h3 class="font-bold">금액</h3>
                            <label id="price-range" class="text-center"></label>
                        </div>
                        <div id="price-slider" class="w-full"></div>
                    </div>
                    <div class="bg-white min-w-60 border px-4 border-gray-300 rounded-md p-2">
                        <label for="quick-calendar" class="block font-bold">임대 기간</label>
                        <input type="text" id="quick-calendar" placeholder="기간을 선택하세요." class="w-full">
                    </div>

                    <button id="find-item" class="py-2 bg-[#3FB8AF] text-white font-bold flex-shrink-0 flex-grow-0 px-4 rounded-md hover:bg-[#2c817c] transition-colors duration-150">
                        조회
                    </button>`;

    if (!placeSt) {
        filterBox.innerHTML = insert;
        createPriceSlider();
        createPlaceSlider();
        createPickCalendar();
        eventSt = false;
        placeSt = true;
        toggle();
    }
}

function findEvent() {
    let insert = `<div class="flex space-x-4">
       <div class="bg-white px-4 py-2 flex-grow border border-gray-300 rounded-md">
        <h3 class="font-bold">연령대</h3>
        <select id="age" class="w-full px-1">
         <option value="all">전체</option>
         <option value="under-10">10세 이하</option>
         <option value="10-19">10~19세</option>
         <option value="20-29">20~29세</option>
         <option value="30-39">30~39세</option>
         <option value="40-49">40~49세</option>
         <option value="50-59">50~59세</option>
         <option value="60-over">60세 이상</option>
        </select>
       </div>
       <div class="bg-white px-4 py-2 flex-shrink-0 border border-gray-300 rounded-md">
        <label for="location" class="font-bold">지역</label>
        <select id="location" class="w-full px-1">
         <option value="all">전체</option>
         <option value="서울">서울</option>
         <option value="부산">부산</option>
         <option value="대구">대구</option>
         <option value="인천">인천</option>
         <option value="광주">광주</option>
         <option value="대전">대전</option>
         <option value="울산">울산</option>
         <option value="세종">세종</option>
         <option value="경기">경기</option>
         <option value="충북">충북</option>
         <option value="충남">충남</option>
         <option value="전북">전북</option>
         <option value="전남">전남</option>
         <option value="경북">경북</option>
         <option value="경남">경남</option>
         <option value="강원">강원</option>
         <option value="제주">제주</option>
        </select>
       </div>
      </div>
      <div class="bg-white px-4 py-2 border border-gray-300 rounded-md">
       <h3 class="font-bold">유형</h3>
       <select id="category" class="w-full px-1">
        <option value="all">전체</option>
        <option value="식품">식품</option>
        <option value="화장품">화장품</option>
        <option value="패션">패션</option>
        <option value="애니">애니</option>
        <option value="아이돌">아이돌</option>
        <option value="스포츠">스포츠</option>
        <option value="음악">음악</option>
        <option value="테크">테크</option>
        <option value="인테리어">인테리어</option>
        <option value="문화/예술">문화/예술</option>
        <option value="게임">게임</option>
        <option value="헬스">헬스</option>
        <option value="음료">음료</option>
        <option value="책">책</option>
        <option value="친환경">친환경</option>
       </select>
      </div>
      <div class="bg-white min-w-60 border px-4 border-gray-300 rounded-md p-2">
       <h3 class="font-bold">임대 기간</h3>
       <input type="text" id="quick-calendar" placeholder="기간을 선택하세요." class="w-full">
      </div>

      <button id="find-item" class="py-2 bg-[#3FB8AF] text-white font-bold flex-shrink-0 flex-grow-0 px-4 rounded-md hover:bg-[#2c817c] transition-colors duration-150">
       조회
      </button>`;
    if (!eventSt) {
        filterBox.innerHTML = insert;
        createPickCalendar();
        placeSt = false;
        eventSt = true;
        toggle();
    }
}

function toggle() {
    placeBox.classList.toggle('translate-y-2');
    placeBox.classList.toggle('text-black');
    placeBox.classList.toggle('text-white');
    placeBox.classList.toggle('bg-[#d8d7d7]');
    placeBox.classList.toggle('bg-[#3FB8AF]');
    eventBox.classList.toggle('translate-y-2');
    eventBox.classList.toggle('text-black');
    eventBox.classList.toggle('text-white');
    eventBox.classList.toggle('bg-[#d8d7d7]');
    eventBox.classList.toggle('bg-[#3FB8AF]');
}

// 인증 상태에 따른 UI 업데이트
function updateAuthUI() {
    const isAuthenticated = TokenUtil.hasToken();
    const loginButton = document.querySelector('#login-button');
    const logoutButton = document.querySelector('#logout-button');
    const myPageButton = document.querySelector('#mypage-button');

    if (isAuthenticated) {
        if (loginButton) loginButton.style.display = 'none';
        if (logoutButton) logoutButton.style.display = 'block';
        if (myPageButton) myPageButton.style.display = 'block';
    } else {
        if (loginButton) loginButton.style.display = 'block';
        if (logoutButton) logoutButton.style.display = 'none';
        if (myPageButton) myPageButton.style.display = 'none';
    }
}

// price slider 생성
function createPriceSlider() {
    const priceSlider = document.getElementById('price-slider');
    if (!priceSlider) return;

    noUiSlider.create(priceSlider, {
        start: [0, 1000000],
        connect: true,
        range: {
            'min': 0,
            'max': 1000000
        },
        step: 10000
    });

    const priceRange = document.getElementById('price-range');

    priceSlider.noUiSlider.on('update', function(values) {
        const min = Math.round(values[0]).toLocaleString();
        const max = Math.round(values[1]).toLocaleString();
        priceRange.textContent = `${min}원 - ${max}원`;
    });
}

// place slider 생성
function createPlaceSlider() {
    const placeSlider = document.getElementById('place-slider');
    if (!placeSlider) return;

    noUiSlider.create(placeSlider, {
        start: [0, 100],
        connect: true,
        range: {
            'min': 0,
            'max': 100
        },
        step: 1
    });

    const placeRange = document.getElementById('place-range');

    placeSlider.noUiSlider.on('update', function(values) {
        const min = Math.round(values[0]);
        const max = Math.round(values[1]);
        placeRange.textContent = `${min}평 - ${max}평`;
    });
}

// calendar 생성
function createPickCalendar() {
    const quickCalendar = document.getElementById('quick-calendar');
    if (!quickCalendar) return;

    flatpickr(quickCalendar, {
        mode: "range",
        minDate: "today",
        dateFormat: "Y.m.d",
        locale: {
            rangeSeparator: " - "
        },
        static: true
    });
}
