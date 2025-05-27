loadNavbar('navContainer');
loadAlerts('alertContainer');
//loadModal('modalContainer');

let isEditing = false;
let currentlyEditingDepartmentId = null;
let tempDepartmentName = null;

document.addEventListener('DOMContentLoaded', () => {
	const modalElement = document.getElementById('deleteDepartmentConfirmModal');
	if (modalElement) {
		deleteDepartmentConfirmModal = new bootstrap.Modal(modalElement);
	}

	// Bind the delete confirm button click event
	const deleteBtn = document.getElementById('deleteDepartmentConfirmButton');
	if (deleteBtn) {
		deleteBtn.addEventListener('click', async () => {
			if (!currentDepartment) return;

			// Get selected department to transfer employees to
			const transferDepartment = document.getElementById('transferDepartmentSelect').value;
			
			console.log('Transfer to Department: ' + transferDepartment);

			try {
				const response = await fetch(`/departments/${currentDepartment.id}/delete?transferDepartment=${encodeURIComponent(transferDepartment)}`, {
					method: 'POST'
				});
				
				const result = await response.json();

				if (response.ok && result.success) {
					showSuccessAlert(`Successfully deleted department <strong>${currentDepartment.name}</strong> and transferred employees to <strong>${transferDepartment}</strong>.`);
					deleteDepartmentConfirmModal.hide();
					currentDepartment = null;
					// Reload your table or apply filters here
					loadDepartments();
				} else {
					showErrorAlert(result.message);
				}
			} catch (error) {
				console.error('Error deleting department:', error);
				showErrorAlert('Error deleting department.');
			}
		});
	}
});

async function openDeleteDepartmentModal(department) {
	currentDepartment = department;
	document.getElementById('departmentNameToDelete').textContent = department.name;

	try {
		const response = await fetch('/departments/all');
		const responseData = await response.json();
		const departments = responseData.departments;
		
		console.log(departments);

		const select = document.getElementById('transferDepartmentSelect');
		select.innerHTML = '';

		departments.forEach(dep => {
			if (dep.id !== department.id) {
				const option = document.createElement('option');
				option.value = dep.name;
				option.textContent = dep.name;
				
				if (dep.name === 'NO DEPARTMENT') {
					option.selected = true;
				}
				
				select.appendChild(option);
			}
		});
		
		if (deleteDepartmentConfirmModal) {
			deleteDepartmentConfirmModal.show();
		}
	} catch (error) {
		console.error('Error loading departments for transfer:', error);
		showErrorAlert('Failed to load departments for transfer.');
	}
}

function displayDepartmentsTable(departments) {
	const tbody = document.getElementById('departmentTableBody');
	tbody.innerHTML = '';
	
	departments.forEach(department => {
		const row = document.createElement('tr');
		
		const idCell = document.createElement('td');
		idCell.className = 'text-center';
		idCell.textContent = 'DEP-' + String(department.id).padStart(3, '0');
		row.appendChild(idCell);
		
		const nameInput = document.createElement('input');
		nameInput.type = 'text';
		nameInput.style.width = '100%';
		nameInput.style.boxSizing = 'border-box';
		nameInput.style.border = '0';
		nameInput.style.textAlign = 'center';
		nameInput.style.backgroundColor = 'transparent';
		nameInput.id = 'departmentNameInput' + department.id;
		nameInput.readOnly = true;
		nameInput.style.pointerEvents = 'none';
		nameInput.value = department.name;
		
		const nameCell = document.createElement('td');
		nameCell.appendChild(nameInput);
		row.appendChild(nameCell);
		
		const noOfEmployeesCell = document.createElement('td');
		noOfEmployeesCell.className = 'text-center';
		noOfEmployeesCell.textContent = department.numberOfEmployees;
		row.append(noOfEmployeesCell);
		
		const actionsCellWrapper = document.createElement('div');
		actionsCellWrapper.className = 'd-flex justify-content-center align-items-center';
		actionsCellWrapper.style.width = '100%';
		
		if (department.name === 'NO DEPARTMENT') {
			const actionsCell = document.createElement('td');
			actionsCell.appendChild(actionsCellWrapper);
			row.appendChild(actionsCell);
			tbody.appendChild(row);
			return;
		}
		
		const cancelButton = document.createElement('button');
		cancelButton.className = 'btn btn-sm btn-secondary me-2';
		cancelButton.style = 'width: 20%; display: none;';
		cancelButton.id = 'cancelDepartmentButton' + department.id;
		cancelButton.textContent = 'Cancel';
		cancelButton.onclick = async () => {
			nameInput.readOnly = true;
			nameInput.style.pointerEvents = 'none';
			nameInput.value = tempDepartmentName;
			tempDepartmentName = null;
			isEditing = false;
			updateActionButtons();
			currentlyEditingDepartmentId = null;
		};
		actionsCellWrapper.appendChild(cancelButton);
		
		const saveButton = document.createElement('button');
		saveButton.className = 'btn btn-sm btn-success';
		saveButton.style = 'width: 20%; display: none;';
		saveButton.id = 'saveDepartmentButton' + department.id;
		saveButton.textContent = 'Save';
		saveButton.onclick = async () => {
			const newName = nameInput.value;
			
			try {
				const response = await fetch(`/departments/${department.id}/edit`, {
					method: 'POST',
					headers: {'Content-Type': 'application/x-www-form-urlencoded'},
					body: new URLSearchParams({ name: newName })
				});
				
				const responseData = await response.json();
				
				if (response.ok && responseData.success) {
					showSuccessAlert(responseData.message);
				} else {
					showErrorAlert(responseData.message);
					nameInput.value = tempDepartmentName;
				}
			} catch (error) {
				console.error('Error: ', error);
				showErrorAlert('Failed to save changes.');
				nameInput.value = tempDepartmentName;
			}
			
			nameInput.readOnly = true;
			nameInput.style.pointerEvents = 'none';
			tempDepartmentName = null;
			
			isEditing = false;
			updateActionButtons();
			currentlyEditingDepartmentId = null;
		}
		actionsCellWrapper.appendChild(saveButton);
		
		const editButton = document.createElement('button');
		editButton.className = 'btn btn-sm btn-outline-secondary me-2';
		editButton.style = 'width: 20%;';
		editButton.id = 'editDepartmentButton' + department.id;
		editButton.textContent = 'Edit';
		editButton.onclick = async () => {
			if (isEditing) {
				return;
			}
			
			tempDepartmentName = nameInput.value;
			isEditing = true;
			currentlyEditingDepartmentId = department.id;
			
			nameInput.readOnly = false;
			nameInput.style.pointerEvents = 'auto';
			nameInput.focus();
			
			updateActionButtons();
		};
		actionsCellWrapper.appendChild(editButton);
		
		const deleteButton = document.createElement('button');
		deleteButton.className = 'btn btn-sm btn-outline-danger';
		deleteButton.style = 'width: 20%;';
		deleteButton.id = 'deleteDepartmentButton' + department.id;
		deleteButton.textContent = 'Delete';
		deleteButton.onclick = async () => {
			currentlyEditingDepartmentId = department.id;
			
			openDeleteDepartmentModal(department);


		};
		actionsCellWrapper.appendChild(deleteButton);
		
		const actionsCell = document.createElement('td');
		actionsCell.appendChild(actionsCellWrapper);
		row.appendChild(actionsCell);
		tbody.appendChild(row);
	});
}

function updateActionButtons() {
	if (!isEditing) {
		document.getElementById('cancelDepartmentButton' + currentlyEditingDepartmentId).style.display = 'none';
		document.getElementById('saveDepartmentButton' + currentlyEditingDepartmentId).style.display = 'none';
		document.getElementById('deleteDepartmentButton' + currentlyEditingDepartmentId).style.display = 'inline-block';
		document.getElementById('editDepartmentButton' + currentlyEditingDepartmentId).style.display = 'inline-block';
	} else {
		document.getElementById('cancelDepartmentButton' + currentlyEditingDepartmentId).style.display = 'inline-block';
		document.getElementById('saveDepartmentButton' + currentlyEditingDepartmentId).style.display = 'inline-block';
		document.getElementById('deleteDepartmentButton' + currentlyEditingDepartmentId).style.display = 'none';
		document.getElementById('editDepartmentButton' + currentlyEditingDepartmentId).style.display = 'none';
	}
}

function addNewDepartmentRow() {
	if (isEditing) {
		return;
	}
	
	const tbody = document.getElementById('departmentTableBody');
	const row = document.createElement('tr');
	
	const idCell = document.createElement('td');
	idCell.className = 'text-center';
	idCell.textContent = 'Auto-generated';
	row.appendChild(idCell);
	
	const nameInput = document.createElement('input');
	nameInput.type = 'text';
	nameInput.style.width = '100%';
	nameInput.style.boxSizing = 'border-box';
	nameInput.style.textAlign = 'center';
	nameInput.style.backgroundColor = 'white';
	nameInput.id = 'newDepartmentNameInput';
	
	const nameCell = document.createElement('td');
	nameCell.appendChild(nameInput);
	row.appendChild(nameCell);
	
	const employeeCountCell = document.createElement('td');
	employeeCountCell.className = 'text-center';
	employeeCountCell.textContent = '0';
	row.appendChild(employeeCountCell);
	
	const actionsCellWrapper = document.createElement('div');
	actionsCellWrapper.className = 'd-flex justify-content-center align-items-center';

	const cancelButton = document.createElement('button');
	cancelButton.className = 'btn btn-sm btn-secondary me-2';
	cancelButton.style = 'width: 20%;';
	cancelButton.textContent = 'Cancel';
	cancelButton.onclick = () => {
		row.remove();
		isEditing = false;
	};
	actionsCellWrapper.appendChild(cancelButton);

	const saveButton = document.createElement('button');
	saveButton.className = 'btn btn-sm btn-success';
	saveButton.style = 'width: 20%;';
	saveButton.textContent = 'Save';
	saveButton.onclick = async () => {
		const newName = nameInput.value.trim();
		if (!newName) {
			showErrorAlert('Department name cannot be empty.');
			return;
		}

		try {
			const response = await fetch('/departments/add', {
				method: 'POST',
				headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
				body: new URLSearchParams({ name: newName })
			});

			const data = await response.json();

			if (response.ok && data.success) {
				showSuccessAlert(data.message);
				loadDepartments();
			} else {
				showErrorAlert(data.message || 'Failed to add department.');
			}
		} catch (error) {
			console.error('Error:', error);
			showErrorAlert('Failed to add department.');
		}
	};
	actionsCellWrapper.appendChild(saveButton);

	const actionsCell = document.createElement('td');
	actionsCell.appendChild(actionsCellWrapper);
	row.appendChild(actionsCell);

	tbody.appendChild(row);
	nameInput.focus();
	isEditing = true;
}

async function loadDepartments() {
	const response = await fetch('/departments/all');
	const responseData = await response.json();
	
	if (response.ok && responseData.success) {
		const departments = responseData.departments;
		
		displayDepartmentsTable(departments);
	} else {
		showErrorAlert(responseData.message);
		console.error(responseData.message);
	}
}

loadDepartments();