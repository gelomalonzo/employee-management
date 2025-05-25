loadNavbar('navContainer');
loadAlerts('alertContainer');
loadModal('modalContainer');

let isEditing = false;

window.onload = () => {
	var departments = fetchDepartments('departmentSelect');
	var employees = fetchAllEmployees();
};

// sorting table
document.addEventListener("DOMContentLoaded", () => {
	const table = document.getElementById("employeeTable");
	const tbody = document.getElementById("employeeTableBody");
	let sortDirection = {};

	table.querySelectorAll("th[data-sort]").forEach(th => {
		th.style.cursor = "pointer";
		th.addEventListener("click", () => {
			const key = th.dataset.sort;
			const direction = sortDirection[key] === "asc" ? "desc" : "asc";
			sortDirection[key] = direction;

			const rows = Array.from(tbody.querySelectorAll("tr"));

			rows.sort((a, b) => {
				let aText = a.querySelector(`td:nth-child(${th.cellIndex + 1})`).textContent.trim();
				let bText = b.querySelector(`td:nth-child(${th.cellIndex + 1})`).textContent.trim();

				if (key === "id" || key === "salary") {
					aText = parseFloat(aText.replace(/[^0-9.-]+/g, ""));
					bText = parseFloat(bText.replace(/[^0-9.-]+/g, ""));
				}

				if (key === "dob") {
					aText = new Date(aText);
					bText = new Date(bText);
				}

				if (aText < bText) return direction === "asc" ? -1 : 1;
				if (aText > bText) return direction === "asc" ? 1 : -1;
				return 0;
			});

			rows.forEach(row => tbody.appendChild(row));
		});
	});
});

function displayHome(averageSalary, averageAge, minAgeRange, maxAgeRange, employees) {
	const noOfEmployeesContainer = document.getElementById('noOfEmployeesContainer');
	noOfEmployeesContainer.innerHTML = employees.length;
	
	const averageSalaryContainer = document.getElementById('averageSalaryContainer');
	averageSalaryContainer.innerHTML = Number(averageSalary).toLocaleString('en-US', {
		style: 'currency',
		currency: 'PHP',
		minimumFractionDigits: 2
	});
	
	const ageRangeContainer = document.getElementById('ageRangeContainer');
	if (minAgeRange === null || minAgeRange === undefined || maxAgeRange === null || maxAgeRange === undefined) {
		ageRangeContainer.innerHTML = '-';
	} else {
		if (minAgeRange === maxAgeRange) {
			ageRangeContainer.innerHTML = minAgeRange;
		} else {
			ageRangeContainer.innerHTML = minAgeRange + ' - ' + maxAgeRange;
		}
	}
	
	const averageAgeContainer = document.getElementById('averageAgeContainer');
	averageAgeContainer.innerHTML = averageAge;
	
	const tbody = document.getElementById('employeeTableBody');
	tbody.innerHTML = '';
	employees.forEach(employee => {
		const row = document.createElement('tr');
		
		const idCell = document.createElement('td');
		idCell.className = 'text-center';
		idCell.textContent = String(employee.id).padStart(5, '0');
		row.appendChild(idCell);
		
		const nameCell = document.createElement('td');
		nameCell.className = 'text-center';
		nameCell.textContent = employee.name;
		row.appendChild(nameCell);
		
		const birthDateCell = document.createElement('td');
		birthDateCell.className = 'text-center';
		const date = new Date(employee.birthDate);
		const formattedDate = date.toLocaleDateString('en-US', {
				year: 'numeric',
				month: 'long',
				day: 'numeric'
		});
		birthDateCell.textContent = formattedDate;
		row.appendChild(birthDateCell);
		
		const departmentCell = document.createElement('td');
		departmentCell.className = 'text-center';
		departmentCell.textContent = employee.department.name;
		row.appendChild(departmentCell);
		
		const salaryCell = document.createElement('td');
		salaryCell.className = 'text-center';
		salaryCell.textContent = Number(employee.salary).toLocaleString('en-US', {
			style: 'currency',
			currency: 'PHP',
			minimumFractionDigits: 2
		});
		row.appendChild(salaryCell);
		
		const actionsCell = document.createElement('td');
		const actionsCellWrapper = document.createElement('div');
		actionsCellWrapper.className = 'd-flex justify-content-center align-items-center';
		const detailsButton = document.createElement('button');
		detailsButton.className = 'btn btn-sm btn-outline-secondary';
		detailsButton.id = 'detailsButton' + employee.id;
		detailsButton.textContent = 'Details';
		detailsButton.onclick = async () => {
			openViewModal(employee);
		};
		actionsCellWrapper.appendChild(detailsButton);
		actionsCell.appendChild(actionsCellWrapper);
		row.appendChild(actionsCell);
		
		tbody.appendChild(row);
	});
}

async function fetchDepartments(containerId) {
	try {
		const response = await fetch('/departments/all');
		const result = await response.json();
		
		if (result.isEmpty) {
			showErrorAlert(result.message, 5000);
			return;
		}
		
		const departments = result.departments;
		const departmentSelect = document.getElementById(containerId);
		
		departments.forEach(department => {
			const option = document.createElement('option');
			option.value = department.name;
			option.textContent = department.name;
			departmentSelect.appendChild(option);
		});
	} catch (error) {
		console.error('Error fetching departments: ', error);
	}
}

async function fetchAllEmployees() {
	try {
		const response = await fetch('/employees/all');
		const result = await response.json();
		
		if (!result.success) {
			showErrorAlert(result.message, 5000);
		}

		if (result.isEmpty) {
			showWarningAlert(result.message, 4000);
		}
		
		displayHome(result.averageSalary, result.averageAge, result.minAgeRange, result.maxAgeRange, result.employees);
	} catch (error) {
		console.error('Error fetching employees: ', error);
	}
}

async function fetchEmployeeId() {
	const searchIdInput = document.getElementById('searchIdInput')
	const employeeId = searchIdInput.value;
	if (employeeId === '') {
		showErrorAlert('Invalid action. Can\'t search an empty employee ID.', 4000);
		return;
	}
	
	if (isNaN(employeeId) || parseInt(employeeId) <= 0) {
		showErrorAlert('Invalid employee ID. Please enter a valid positive number.', 4000);
		searchIdInput.value = '';
		return;
	}
	
	const searchNameInput = document.getElementById('searchNameInput');
	searchNameInput.innerHTML = '';
	
	const departmentSelect = document.getElementById('departmentSelect');
	departmentSelect.innerHTML = '';
	const defaultDepartmentOption = document.createElement('option');
	defaultDepartmentOption.value = '';
	defaultDepartmentOption.textContent = 'ALL DEPARTMENTS';
	departmentSelect.appendChild(defaultDepartmentOption);
	fetchDepartments('departmentSelect');
	
	const minAgeInput = document.getElementById('minAgeInput');
	minAgeInput.value = '';
	
	const maxAgeInput = document.getElementById('maxAgeInput');
	maxAgeInput.value = '';
	
	try {
		const response = await fetch(`/employees/${employeeId}`);
		const result = await response.json();
		
		if (!result.success) {
			showErrorAlert(result.message, 5000);
		}

		if (result.isEmpty) {
			showWarningAlert(result.message, 4000);
		}
		
		displayHome(result.averageSalary, result.averageAge, result.minAgeRange, result.maxAgeRange, result.employees);
	} catch (error) {
		console.error('Error fetching employee by ID: ', error);
	}
}

async function applyFilters() {
	const searchIdInput = document.getElementById('searchIdInput');
	searchIdInput.value = '';
	
	const employeeName = document.getElementById('searchNameInput').value;
	const departmentName = document.getElementById('departmentSelect').value;
	const minAge = document.getElementById('minAgeInput').value;
	const maxAge = document.getElementById('maxAgeInput').value;
	
	if (departmentName === 'ALL DEPARTMENTS' || departmentName === '') {
		var departmentId = '';
	} else {
		try {
			const departmentResponse = await fetch(`/departments/getIdByName?departmentName=${encodeURIComponent(departmentName)}`);
			const departmentResult = await departmentResponse.json();
			
			console.log(departmentResult);
			departmentId = departmentResult.departmentId;
		} catch (error) {
			console.error('Error fetching department ID: ', error);
		}
	}
	
	const params = new URLSearchParams({
		name: employeeName,
		departmentId: departmentId,
		minAge: minAge,
		maxAge: maxAge
	});
	
	try {
		const response = await fetch(`/employees/filter?${params.toString()}`);
		const result = await response.json();
		
		if (!result.success) {
			showErrorAlert(result.message, 5000);
		}
		
		if (result.isEmpty) {
			showWarningAlert(result.message, 4000);
		}
		
		displayHome(result.averageSalary, result.averageAge, result.minAgeRange, result.maxAgeRange, result.employees);
	} catch (error) {
		console.error('Error fetching filtered employee list: ', error);
	}
}

async function resetFilters() {
	const searchIdInput = document.getElementById('searchIdInput');
	searchIdInput.value = '';
	
	const searchNameInput = document.getElementById('searchNameInput');
	searchNameInput.value = '';
	
	const departmentSelect = document.getElementById('departmentSelect');
	departmentSelect.innerHTML = '';
	const defaultDepartmentOption = document.createElement('option');
	defaultDepartmentOption.value = '';
	defaultDepartmentOption.textContent = 'ALL DEPARTMENTS';
	departmentSelect.appendChild(defaultDepartmentOption);
	fetchDepartments('departmentSelect');
	
	const minAgeInput = document.getElementById('minAgeInput');
	minAgeInput.value = '';
	
	const maxAgeInput = document.getElementById('maxAgeInput');
	maxAgeInput.value = '';
	
	fetchAllEmployees();
}
