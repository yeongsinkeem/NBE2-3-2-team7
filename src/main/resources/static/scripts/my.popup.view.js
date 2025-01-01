const popupSeq = window.location.pathname.split('/').pop();

const thumbnailBox = document.getElementById('thumbnail-image');
const titleInput = document.getElementById('popup-title');
const categoryInput = document.getElementById('category');
const locationInput = document.getElementById('location');
const periodInput = document.getElementById('quick-calendar');
const ageInput = document.getElementById('age');
const areaRangeInput = document.getElementById('place-range');
const descriptionInput = document.getElementById('detail-text');

document.addEventListener('DOMContentLoaded', () => {
	init();
});

function init() {
	createPickCalendar();
	createPlaceSlider();
	fetch(`/api/popup/${popupSeq}`)
		.then(resp=> resp.json())
		.then(res => {
			console.log(res);


			descriptionInput.value = res.description;
		})
		.catch(err => console.log(err));
}

function deletePopupStore() {
	fetch(`/api/popup/${popupSeq}`, {
		method: 'DELETE',
		headers: {
			'Content-Type': 'application/json',
		}
	})
		.then(resp=> {
			if (resp.ok) {
				window.location.href = '/mypage/popup';
			}
		})
		.catch(err => {
			console.log(err)
			window.location.href = '/mypage/popup';
		});
}