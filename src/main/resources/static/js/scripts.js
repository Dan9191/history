document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('patientForm');
    const spinnerOverlay = document.getElementById('spinnerOverlay');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');
    const patientFormCollapse = document.getElementById('patientFormCollapse');
    const deleteButtons = document.querySelectorAll('.delete-patient');

    // Обработчик создания пациента
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        spinnerOverlay.style.display = 'flex';
        errorMessage.textContent = '';
        successMessage.textContent = '';

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
                spinnerOverlay.style.display = 'none';
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
                    method: 'DELETE'
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(err => {
                                throw new Error(err.message || 'Ошибка при удалении пациента');
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        spinnerOverlay.style.display = 'none';
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