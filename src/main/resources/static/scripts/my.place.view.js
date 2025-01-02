const placeSeq = window.location.pathname.split('/').pop();

document.addEventListener('DOMContentLoaded', () => {

});

let infraList = [];

function addInfra(btn) {
	const val = btn.dataset.val;
	if (!infraList.includes(val))	{
		infraList.push(val);
		renderInfra();
	}
}

function renderInfra() {
	const infraBox = document.getElementById('my-infra-box');

	let inner = '';
	infraList.forEach((val, index)=> {
		inner += `<li class="text-xs px-3 py-2 flex hover:border-red-500 transition-colors items-center border-2 border-[#3fb8af] rounded-full space-x-2">
									<span class="font-bold">${val}</span>
									<button type="button" onclick="removeInfra(${index})">&#10060;</button>
								</li>`;
	})
	infraBox.innerHTML = inner;
}

function removeInfra(index) {
	infraList.splice(index, 1);
	renderInfra();
}

function deleteRentalPlace() {
	fetch(`/api/rental/${placeSeq}`, {
		method: 'DELETE'
	})
		.catch(err => {
			alert('삭제중 에러가 발생했습니다.');
			console.log(err)
		})
		.finally(() => {
			window.location.href = '/mypage/rental';
		});
}