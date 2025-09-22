document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('diagnosisForm');
    const errorMessage = document.getElementById('errorMessage');
    const successMessage = document.getElementById('successMessage');
    const diagnosisFormCollapse = document.getElementById('diagnosisFormCollapse');
    const deleteButtons = document.querySelectorAll('.delete-diagnosis');

    // Обработчик создания диагноза
    form.addEventListener('submit', function (event) {
        event.preventDefault();
        errorMessage.textContent = '';
        successMessage.textContent = '';

        const formData = new FormData(form);
        const data = {};
        formData.forEach((value, key) => {
            data[key] = value;
        });

        fetch('/diagnoses', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        })
            .then(response => {
                if (!response.ok) {
                    return response.json().then(err => {
                        throw new Error(err.message || 'Ошибка при создании диагноза');
                    });
                }
                return response.json();
            })
            .then(data => {
                successMessage.textContent = 'Диагноз успешно создан!';
                form.reset();
                const collapseInstance = bootstrap.Collapse.getInstance(diagnosisFormCollapse) || new bootstrap.Collapse(diagnosisFormCollapse, { toggle: false });
                collapseInstance.hide();
                setTimeout(() => window.location.reload(), 2000);
            })
            .catch(error => {
                errorMessage.textContent = error.message;
            });
    });

    // Обработчик удаления диагноза
    deleteButtons.forEach(button => {
        button.addEventListener('click', function () {
            const diagnosisId = this.getAttribute('data-diagnosis-id');
            if (confirm('Вы уверены, что хотите удалить диагноз?')) {
                fetch(`/diagnoses/${diagnosisId}`, {
                    method: 'DELETE'
                })
                    .then(response => {
                        if (!response.ok) {
                            return response.json().then(err => {
                                throw new Error(err.message || 'Ошибка при удалении диагноза');
                            });
                        }
                        return response.json();
                    })
                    .then(data => {
                        alert('Диагноз успешно удалён!');
                        window.location.reload();
                    })
                    .catch(error => {
                        alert('Ошибка: ' + error.message);
                    });
            }
        });
    });
});