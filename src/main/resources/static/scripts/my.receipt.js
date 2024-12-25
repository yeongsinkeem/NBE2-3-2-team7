const receipts = document.getElementById('receipt-list');
let result = '';

document.addEventListener('DOMContentLoaded', () => {
    init();
})

function init() {
    fetch('/api/receipt', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${1234}`,
        }
    })
        .then(resp => resp.json())
        .then(res => {
            res.data.forEach((item) => {
                renderReceipts(item)
            })
            receipts.innerHTML = result;
        })
        .catch(err => console.log(err));
}

function refundBtn(orderId, placeName) {
    viewDeleteModal(orderId, placeName);
}

function viewDeleteModal(orderId, placeName) {
    const modal = document.getElementById('modal-box');

    modal.innerHTML = `<div class="w-full min-h-screen flex justify-center items-center fixed z-10" id="modal-check" onclick="closeModal()">
                    <div class="bg-white p-4 space-y-4 max-w-2xl flex flex-col rounded-lg border border-gray-300" id="modal-bubble">
                        <div class="flex justify-between">
                            <em class="font-bold text-xs flex items-center">Info</em>
                            <button onclick="closeModal()">&#10060;</button>
                        </div>
                        <hr>
                        <div class="text-center">
                            <h5 class="font-bold text-xl">${placeName}</h5>
                            <h5 class="font-bold text-xl">예약을 취소하시겠습니까?</h5>
                        </div>
                        <div class="justify-center flex mt-3">
                            <button onclick="refundAction('${orderId}')" class="px-4 py-2 text-sm font-bold text-white bg-[#3fb8af] rounded-xl hover:bg-[#2c817c] transition-colors">예약 취소</button>
                        </div>
                    </div>
                </div>`;
    document.getElementById('modal-bubble').addEventListener('click', function (e) {
        e.stopPropagation();
    })
}

function closeModal() {
    const modal = document.getElementById('modal-check');
    modal.remove();
}

function refundAction(orderId) {
    result = '';
    fetch(`/api/receipt/${orderId}`, {
        method: 'PUT',
        headers: {
            'ContentType': 'application/json',
            'Authorization': `Bearer ${1}`
        },
    })
        .then(() => {
            window.location.reload();
        })
        .catch(err => console.log(err))
}

function renderReceipts(item) {
    switch (item.reservationStatus) {
        case '결제 완료':
            result += `<div class="p-4 justify-between space-y-2 bg-white transition-colors hover:border-gray-700 flex flex-col border border-gray-300 rounded-lg">
                    <div class="flex justify-between">
                        <span class="font-bold text-sm text-gray-700">${item.reservationStatus}</span>
                        <button onclick="refundBtn('${item.orderId}','${item.rentalPlaceName}')" class="font-bold text-red-500 hover:text-red-700 text-xs">예약 취소</button>
                    </div>
                    <div class="flex sm:items-center sm:justify-between flex-col sm:flex-row">
                        <span class="font-bold text-xl sm:mt-0 mt-2">${item.rentalPlaceName}</span>
                        <span class="font-bold text-sm sm:mt-0 mt-4">${item.price.toLocaleString()}원 / 일</span>
                    </div>
                    <div class="flex justify-between flex-col-reverse sm:flex-row">
                        <span class="font-bold text-lg mt-2 sm:mt-0">${getRangeDate(item.startDate,item.endDate)} (${getPeriod(item.startDate,item.endDate)}일)</span>
                        <span class="font-bold text-lg">총 금액 ${item.totalAmount.toLocaleString()}원</span>
                    </div>
                </div>`
            break
        case '임대 완료':
            result += `<div class="p-4 justify-between space-y-2 transition-colors hover:bg-blue-300 bg-white flex flex-col border border-gray-300 rounded-lg">
                    <div class="flex justify-start">
                        <span class="font-bold text-sm text-[#3FB8AF]">${item.reservationStatus}</span>
                    </div>
                    <div class="flex sm:items-center sm:justify-between flex-col sm:flex-row">
                        <span class="font-bold text-xl sm:mt-0 mt-2">${item.rentalPlaceName}</span>
                        <span class="font-bold text-sm sm:mt-0 mt-4">${item.price.toLocaleString()}원 / 일</span>
                    </div>
                    <div class="flex justify-between flex-col-reverse sm:flex-row">
                        <span class="font-bold text-lg mt-2 sm:mt-0">${getRangeDate(item.startDate,item.endDate)} (${getPeriod(item.startDate,item.endDate)}일)</span>
                        <span class="font-bold text-lg">총 금액 ${item.totalAmount.toLocaleString()}원</span>
                    </div>
                </div>`
            break
        case '환불 완료':
            result += `<div class="p-4 justify-between space-y-2 transition-colors hover:border-red-500 bg-gray-200 flex flex-col border border-gray-300 rounded-lg">
                    <div class="flex justify-start">
                        <span class="font-bold text-sm text-red-500">${item.reservationStatus}</span>
                    </div>
                    <div class="flex sm:items-center sm:justify-between flex-col sm:flex-row">
                        <span class="font-bold text-xl sm:mt-0 mt-2">${item.rentalPlaceName}</span>
                        <span class="font-bold text-sm sm:mt-0 mt-4">${item.price.toLocaleString()}원 / 일</span>
                    </div>
                    <div class="flex justify-between flex-col-reverse sm:flex-row">
                        <span class="font-bold text-lg mt-2 sm:mt-0">${getRangeDate(item.startDate,item.endDate)} (${getPeriod(item.startDate,item.endDate)}일)</span>
                        <span class="font-bold text-lg">총 금액 ${item.totalAmount.toLocaleString()}원</span>
                    </div>
                </div>`
            break
    }
}