document.addEventListener('DOMContentLoaded', () => {

})

document.getElementsByName('status-switch').forEach(status => {
    status.addEventListener('change', (e) => {
        let flag = e.target.checked;
        fetch("/~~", {
            method: "PUT",
            body: JSON.stringify({
                flag: flag,
            })
        })
            .then(res => res.json())
            .then(res => {

            })
    })
})