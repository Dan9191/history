document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('patientForm');
    const spinnerOverlay = document.getElementById('spinnerOverlay');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Предотвратить стандартную отправку формы
        spinnerOverlay.style.display = 'flex'; // Показать спиннер
        errorMessage.textContent = ''; // Очистить сообщение об ошибке
        successMessage.textContent = ''; // Очистить сообщение об успехе

        const formData = new FormData(form);
        fetch('/patients', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Ошибка при создании пациента');
                    });
                }
                return response.json();
            })
            .then(data => {
                spinnerOverlay.style.display = 'none'; // Скрыть спиннер
                successMessage.textContent = 'Пациент успешно создан!';
                form.reset(); // Очистить форму
                setTimeout(() => window.location.reload(), 2000); // Перезагрузка через 2 секунды
            })
            .catch(error => {
                spinnerOverlay.style.display = 'none'; // Скрыть спиннер
                errorMessage.textContent = error.message;
            });
    });
});