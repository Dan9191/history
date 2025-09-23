document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('patientForm');
    const spinnerOverlay = document.getElementById('spinnerOverlay');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');
    const patientFormCollapse = document.getElementById('patientFormCollapse');
    const deleteButtons = document.querySelectorAll('.delete-patient');

    // Получить CSRF-токен
    const csrfToken = document.querySelector('meta[name="_csrf"]')?.getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content');

    if (!csrfToken || !csrfHeader) {
        console.error('CSRF token or header not found');
        errorMessage.textContent = 'Ошибка: CSRF-токен не найден';
        return;
    }

    // Обработчик создания пациента
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        spinnerOverlay.style.display = 'flex';
        errorMessage.textContent = '';
        successMessage.textContent = '';

        const formData = new FormData(form);
        const data = {};
        formData.forEach((value, key) => {
            if (key === 'diagnoses') {
                if (!data[key]) data[key] = [];
                data[key].push(value);
            } else if (key === 'file') {
                // Пропускаем файл, так как он не обрабатывается в JSON
            } else {
                data[key] = value;
            }
        });

        fetch('/patients', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                spinnerOverlay.style.display = 'none';
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Ошибка при создании пациента');
                    });
                }
                return response.json();
            })
            .then(data => {
                successMessage.textContent = 'Пациент успешно создан!';
                form.reset();
                const collapseInstance = bootstrap.Collapse.getInstance(patientFormCollapse) || new bootstrap.Collapse(patientFormCollapse, { toggle: false });
                collapseInstance.hide();
                setTimeout(() => window.location.reload(), 2000);
            })
            .catch(error => {
                spinnerOverlay.style.display = 'none';
                errorMessage.textContent = error.message;
            });
    });

    // Обработчик удаления пациента
    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            const patientId = this.getAttribute('data-patient-id');
            if (confirm('Вы уверены, что хотите удалить пациента?')) {
                spinnerOverlay.style.display = 'flex';
                fetch(`/patients/${patientId}`, {
                    method: 'DELETE',
                    headers: {
                        [csrfHeader]: csrfToken
                    }
                })
                    .then(response => {
                        spinnerOverlay.style.display = 'none';
                        if (!response.ok) {
                            return response.json().then(err => {
                                throw new Error(err.message || 'Ошибка при удалении пациента');
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        alert('Пациент успешно удалён!');
                        window.location.reload();
                    })
                    .catch(error => {
                        spinnerOverlay.style.display = 'none';
                        alert('Ошибка: ' + error.message);
                    });
            }
        });
    });
});