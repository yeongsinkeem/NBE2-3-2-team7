const popupSeq = window.location.pathname.split('/').pop();

const title = document.getElementById('popup-title');
const imagesBox = document.getElementById('image-container');
const place = document.getElementById('target-place');
const type = document.getElementById('popup-type');
const rentalDate = document.getElementById('rental-date');
const age = document.getElementById('target-age');
const area = document.getElementById('target-area');
const description = document.getElementById('popup-description');

window.addEventListener('DOMContentLoaded', ()=> {
	init();
})

function init() {
	fetch(`/api/popup/${popupSeq}`)
		.then(resp=> resp.json())
		.then(res => {
			let result = '';
			if (res.images.length > 0) {
				res.images.forEach((image) => {
					result += `<div class="w-full h-full flex-shrink-0">
					<img src="/images/popup_detail_images/${image}" class="mx-auto h-full w-full object-cover" alt="">
				</div>`
				})
			} else {
				imagesBox.innerHTML = `<div class="w-full h-full flex-shrink-0">
					<img src="" class="mx-auto h-full w-full object-cover" alt="">
				</div>`;
			}

			title.innerHTML = res.popupStore.title;
			place.innerHTML = res.popupStore.targetLocation;
			type.innerHTML = res.popupStore.type;
			rentalDate.innerHTML = `${res.popupStore.startDate} ~ ${res.popupStore.endDate}`;
			age.innerHTML = res.popupStore.targetAgeGroup;
			description.innerHTML = res.popupStore.description;

			slideImage();
		})
		.catch(err => console.log(err));
}

function slideImage() {
	const slides = document.getElementById('image-container');
	const slideItems = document.querySelectorAll('#image-container > div');

	let index = 0;

	const totalSlides = slideItems.length;

	const prevButton = document.getElementById('prev-img-btn');
	const nextButton = document.getElementById('next-img-btn');

	const num = document.getElementById('slide-img-num');
	const total = document.getElementById('slide-img-total');
	num.innerHTML = 1;
	total.innerHTML = totalSlides;

	prevButton.addEventListener('click', () => {
			index = (index - 1 + totalSlides) % totalSlides;
			num.innerHTML = index+1;
			moveSlide(slides, index);
	});

	nextButton.addEventListener('click', () => {
			index = (index + 1) % totalSlides;
			num.innerHTML = index+1;
			moveSlide(slides, index);
	});

	function moveSlide(slides, index) {
		slides.style.transform = `translateX(-${index * 100}%)`;
	}
}
