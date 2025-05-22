let currentEmployee = null;

async function loadModal(containerId) {
	const response = await fetch('/component/employee-modal.html');
	const html = await response.text();
	document.getElementById(containerId).innerHTML = html;

	const modalElement = document.getElementById('employeeDetailsModal');
	if (modalElement) {
		bootstrapModal = new bootstrap.Modal(modalElement);
	}
}

function openModal(title) {
	if (!currentEmployee) {
		console.log('Adding new employee...');
		document.getElementById('idModalInput').value = 'Auto';
		document.getElementById('nameModalInput').value = '';
		document.getElementById('birthDateModalInput').value = '';
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
	document.getElementById('salaryModalInput').value = `${Number(currentEmployee.salary).toLocaleString()}`;
	
	
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
	document.getElementById('nameModalInput').readOnly = !isEditing;
	document.getElementById('birthDateModalInput').readOnly = !isEditing;
	document.getElementById('departmentModalSelect').disabled = !isEditing;
	document.getElementById('salaryModalInput').readOnly = !isEditing;
	
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

async function saveChanges() {	
	const name = document.getElementById('nameModalInput').value;
	const birthDateStr = document.getElementById('birthDateModalInput').value;
	const salaryStr = document.getElementById('salaryModalInput').value.replace(/[,]/g, '');
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

	// Convert birthDate to ISO format
	const birthDate = new Date(birthDateStr);
	if (isNaN(birthDate)) {
		console.error("Invalid birth date format.");
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
				showSuccessAlert(result.message);
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
	console.log("Discarding changes...");
	openViewModal(currentEmployee);
}

async function deleteEmployee() {
	if (!currentEmployee) {
		return;
	}
	
	try {
		const response = await fetch(`/employees/${currentEmployee.id}/delete`, {method: 'DELETE'});
		const result = await response.json();
		
		if (response.ok && result.success) {
			showSuccessAlert('Successfully deleted employee.');
			window.location.href = result.redirect;
		}
	} catch (error) {
		console.error('Error deleting employee: ', error);
	}
}

function closeModal() {
	setFormEditable(false);
	
	if (bootstrapModal) {
		bootstrapModal.hide();
	}
}

function renderActionButtons(isEditing) {
	const buttonContainer = document.getElementById('employeeActionButtons');
	buttonContainer.innerHTML = '';

	if (isEditing) {
		const cancelButton = document.createElement('button');
		cancelButton.className = 'btn btn-secondary';
		cancelButton.textContent = 'Cancel';
		cancelButton.onclick = () => cancelEdit();
		buttonContainer.appendChild(cancelButton);

		const saveButton = document.createElement('button');
		saveButton.className = 'btn btn-success';
		saveButton.textContent = 'Save';
		saveButton.onclick = () => saveChanges();
		buttonContainer.appendChild(saveButton);
	} else {
		const editButton = document.createElement('button');
		editButton.className = 'btn btn-primary';
		editButton.textContent = 'Edit';
		editButton.onclick = () => openEditModal('Employee Details');
		buttonContainer.appendChild(editButton);

		const deleteButton = document.createElement('button');
		deleteButton.className = 'btn btn-danger';	// btn-error is not Bootstrap standard, use btn-danger
		deleteButton.textContent = 'Delete';
		deleteButton.onclick = () => deleteEmployee();
		buttonContainer.appendChild(deleteButton);
	}
}