
document.addEventListener('DOMContentLoaded', function () {


    document.querySelectorAll('.role-card').forEach(function (card) {
        card.addEventListener('click', function () {
            document.querySelectorAll('.role-card').forEach(c => c.classList.remove('selected'));
            card.classList.add('selected');
        });
    });


    document.querySelectorAll('.alert').forEach(function (alert) {
        setTimeout(function () {
            alert.style.opacity = '0';
            alert.style.transition = 'opacity 0.5s';
            setTimeout(() => alert.remove(), 500);
        }, 4000);
    });


    document.querySelectorAll('form[onsubmit]').forEach(function (form) {

    });
});
