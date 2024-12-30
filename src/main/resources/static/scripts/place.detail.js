window.addEventListener('DOMContentLoaded', ()=> {
	slideImage();
	createMap();
	createPickCalendar();
});

function reserve() {
	// const placeSeq =  window.location.pathname.split('/').pop();
	const placeSeq = 1;

	const rentalDate = document.getElementById('quick-calendar').value.split(' ~ ');

	if (rentalDate[0] === '' || rentalDate[1] === '' ||
		rentalDate[0] === null || rentalDate[1] === null ||
		rentalDate[0] === undefined || rentalDate[1] === undefined) {
		alert('올바른 기간을 선택해주세요.');
	} else {
		window.location.href=`/payment?place=${placeSeq}&start=${rentalDate[0]}&end=${rentalDate[1]}`;
	}
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
