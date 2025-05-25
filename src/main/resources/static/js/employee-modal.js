let currentEmployee = null;
let deleteConfirmModal = null;

async function loadModal(containerId) {
	const response = await fetch('/component/employee-modal.html');
	const html = await response.text();
	document.getElementById(containerId).innerHTML = html;

	const modalElement = document.getElementById('employeeDetailsModal');
	if (modalElement) {
		bootstrapModal = new bootstrap.Modal(modalElement);
	}
	
	const deleteModalElement = document.getElementById('deleteConfirmModal');
	if (deleteModalElement) {
        deleteConfirmModal = new bootstrap.Modal(deleteModalElement);
    }
	
	const deleteConfirmButton = document.getElementById('deleteConfirmButton');
	if (deleteConfirmButton) {
        deleteConfirmButton.addEventListener('click', async () => {
            if (!currentEmployee) {
				return;
			}
			
			try {
				const response = await fetch(`/employees/${currentEmployee.id}/delete`, { method: 'DELETE' });
				const result = await response.json();

				if (response.ok && result.success) {
					showSuccessAlert('Successfully deleted ' + currentEmployee.name + ' from the employee records.');
					deleteConfirmModal.hide();
					closeModal();
					applyFilters();
				}
			} catch (error) {
				showErrorAlert('Error deleting employee.');
				console.error('Error deleting employee: ', error);
			}
        });
    }
}

function openModal(title) {
	if (!currentEmployee) {
		document.getElementById('idModalInput').value = 'Auto';
		document.getElementById('nameModalInput').value = '';
		
		const birthDateModalInput = document.getElementById('birthDateModalInput');
		const today = new Date();
		const minDate = new Date(today.getFullYear() - 100, today.getMonth(), today.getDate()).toISOString().split('T')[0];
		const maxDate = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate()).toISOString().split('T')[0];
		const formatDate = (date) => date.toISOString().split('T')[0];
		birthDateModalInput.setAttribute('min', minDate);
		birthDateModalInput.setAttribute('max', maxDate);
		
		document.getElementById('ageModalInput').value = '';
		document.getElementById('salaryModalInput').value = '';
		
		const departmentModalSelect = document.getElementById('departmentModalSelect');
		departmentModalSelect.innerHTML = '';
		fetchDepartments('departmentModalSelect');

		document.getElementById('modalTitle').innerText = title;

		if (bootstrapModal) {
			bootstrapModal.show();
		}
		
		return;
	}
	
	document.getElementById('idModalInput').value = String(currentEmployee.id).padStart(5, '0');
	document.getElementById('nameModalInput').value = currentEmployee.name;
	document.getElementById('birthDateModalInput').value = new Date(currentEmployee.birthDate).toISOString().split('T')[0];
	document.getElementById('ageModalInput').value = '';
	document.getElementById('ageModalInput').value = currentEmployee.age ?? 'N/A';
	document.getElementById('salaryModalInput').value = Number(currentEmployee.salary).toLocaleString('en-PH', {
		minimumFractionDigits: 2
	});
	
	
	const departmentModalSelect = document.getElementById('departmentModalSelect');
	departmentModalSelect.innerHTML = '';
	fetchDepartments('departmentModalSelect');
	departmentModalSelect.value = currentEmployee.department.name;

	document.getElementById('modalTitle').innerText = title;

	if (bootstrapModal) {
		bootstrapModal.show();
	} else {
		console.error("Bootstrap modal instance not initialized");
	}
}

function setFormEditable(isEditing) {
	document.getElementById('nameModalInput').disabled = !isEditing;
	document.getElementById('birthDateModalInput').disabled = !isEditing;
	document.getElementById('departmentModalSelect').disabled = !isEditing;
	document.getElementById('salaryModalInput').disabled = !isEditing;
	
	renderActionButtons(isEditing);
}

function openViewModal(employee) {
	setFormEditable(false);
	currentEmployee = employee;
	console.log('Current Employee in openViewModal(): ' + employee.id);
	openModal('Employee Details');
}

function openEditModal(title) {
	setFormEditable(true);
	employee = currentEmployee;
	openModal(title, employee);
}

function openAddModal(title) {
	setFormEditable(true);
    currentEmployee = null;
    openModal(title);
}

async function saveChanges() {	
	const name = document.getElementById('nameModalInput').value;
	const birthDateStr = document.getElementById('birthDateModalInput').value;
	const salaryStr = document.getElementById('salaryModalInput').value.replace(/[₱,]/g, '');
	const departmentName = document.getElementById('departmentModalSelect').value;
	let departmentId = null;
	
	try {
		const departmentResponse = await fetch(`/departments/getIdByName?departmentName=${encodeURIComponent(departmentName)}`);
		const departmentResult = await departmentResponse.json();

		console.log(departmentResult);
		departmentId = departmentResult.departmentId;
	} catch (error) {
		console.error('Error fetching department ID.', error);
	}

	const birthDate = new Date(birthDateStr);
	if (isNaN(birthDate)) {
		showErrorAlert('Invalid birth date format.');
		console.error("Invalid birth date format.");
		return;
	}
	
	if (calculateAge(birthDate) < 18 || calculateAge(birthDate) > 100) {
		showErrorAlert('Invalid age. Employee must be at least 18 years old and not older than 100 years old.', 5000);
        console.error("Invalid age.");
        return;
	}
	
	const formParams = new URLSearchParams();
	formParams.append('name', name.trim());
	formParams.append('birthDate', birthDateStr);
	formParams.append('salary', salaryStr);
	formParams.append('departmentId', departmentId);
	
	if (!currentEmployee) {
		try {
			const response = await fetch('/employees/add', {
				method: 'POST',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				body: formParams.toString()
			});
			
			const result = await response.json();

			if (result.success) {
				showSuccessAlert(result.message, 4000);
				currentEmployee = result.savedEmployee;
			} else {
				showErrorAlert(result.message);
			}
		} catch (error) {
			showErrorAlert('Error adding employee.');
			console.error('Error adding employee: ', error);
		}
	} else {
		try {
			const response = await fetch(`/employees/${currentEmployee.id}/edit`, {
				method: 'POST',
				headers: {'Content-Type': 'application/x-www-form-urlencoded'},
				body: formParams.toString()
			});
			
			const result = await response.json();

			if (result.success) {
				showSuccessAlert(result.message);
				currentEmployee = result.savedEmployee;
			} else {
				showErrorAlert(result.message);
			}
		} catch (error) {
			showErrorAlert('Error updating employee details.');
			console.error('Error updating employee details: ', error);
		}
	}
	
	openViewModal(currentEmployee);
	applyFilters();
}

function cancelEdit() {
	if (currentEmployee) {
		openViewModal(currentEmployee);
	} else {
		closeModal();
	}
}

function deleteEmployee() {
	if (!currentEmployee) {
		return;
	}
	
	document.getElementById('employeeNameToDelete').textContent = currentEmployee.name;

	if (deleteConfirmModal) {
		deleteConfirmModal.show();
	}
}

function closeModal() {
	setFormEditable(false);
	
	if (bootstrapModal) {
		bootstrapModal.hide();
	}
	
	currentEmployee = null;
}

function renderActionButtons(isEditing) {
	const buttonContainer = document.getElementById('employeeActionButtons');
	buttonContainer.innerHTML = '';

	if (isEditing) {
		const cancelButton = document.createElement('button');
		cancelButton.className = 'btn btn-secondary';
		cancelButton.style = 'width: 20%;';
		cancelButton.textContent = 'Cancel';
		cancelButton.onclick = () => cancelEdit();
		buttonContainer.appendChild(cancelButton);

		const saveButton = document.createElement('button');
		saveButton.className = 'btn btn-success';
		saveButton.style = 'width: 20%;';
		saveButton.textContent = 'Save';
		saveButton.onclick = () => saveChanges();
		buttonContainer.appendChild(saveButton);
	} else {
		const deleteButton = document.createElement('button');
		deleteButton.className = 'btn btn-danger';
		deleteButton.style = 'width: 20%;';
		deleteButton.textContent = 'Delete';
		deleteButton.onclick = () => deleteEmployee();
		buttonContainer.appendChild(deleteButton);
		
		const editButton = document.createElement('button');
		editButton.className = 'btn btn-primary';
		editButton.style = 'width: 20%;';
		editButton.textContent = 'Edit';
		editButton.onclick = () => openEditModal('Edit Employee Details');
		buttonContainer.appendChild(editButton);
	}
}

function calculateAge(birthDate) {
	const today = new Date();
	let age = today.getFullYear() - birthDate.getFullYear();

	const hasHadBirthdayThisYear =
		today.getMonth() > birthDate.getMonth() ||
		(today.getMonth() === birthDate.getMonth() && today.getDate() >= birthDate.getDate());

	if (!hasHadBirthdayThisYear) {
		age--;
	}

	return age;
}