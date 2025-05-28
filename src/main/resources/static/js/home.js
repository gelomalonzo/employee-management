loadNavbar('navContainer');
loadAlerts('alertContainer');
loadModal('modalContainer');

let isEditing = false;

// for pagination
let currentPage = 0;
const pageSize = 5; // five records per page
let totalPages = 0;
let isFilterActive = false;

// for sorting
let sortField = 'id';
let sortDirection = 'asc';

window.onload = () => {
	var departments = fetchDepartments('departmentSelect', 'ALL DEPARTMENTS');
	var employees = fetchAllEmployees();
};

// sorting table
document.addEventListener("DOMContentLoaded", () => {
	const table = document.getElementById("employeeTable");
	const tbody = document.getElementById("employeeTableBody");
	let sortHeaderDirection = {};

	table.querySelectorAll("th[data-sort]").forEach(th => {
		th.style.cursor = "pointer";
		th.addEventListener("click", () => {
			const key = th.dataset.sort;
			const direction = sortHeaderDirection[key] === "asc" ? "desc" : "asc";
			sortHeaderDirection[key] = direction;
			
			sortField = key;
			sortDirection = direction;
			
			table.querySelectorAll("th[data-sort]").forEach(header => {
				header.innerHTML = header.textContent.trim();
			});
			
			th.innerHTML = `${th.textContent.trim()} <i class="bi ${direction === 'asc' ? 'bi-caret-up-fill' : 'bi-caret-down-fill'} ms-1"></i>`;

			currentPage = 0;

			if (isFilterActive) {
				applyFilters();
			} else {
				fetchAllEmployees();
			}
		});
	});
});

function displayHome(noOfEmployees, averageSalary, averageAge, minAgeRange, maxAgeRange, employees) {
	const noOfEmployeesContainer = document.getElementById('noOfEmployeesContainer');
	noOfEmployeesContainer.innerHTML = noOfEmployees;
	
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
		idCell.textContent = 'EMP-' + String(employee.id).padStart(5, '0');
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

function renderPagination() {
	const container = document.getElementById('paginationContainer');
	container.innerHTML = '';

	if (totalPages <= 1) {
		return;
	}
	
	const firstBtn = document.createElement('button');
	firstBtn.className = 'btn btn-outline-secondary mx-1';
	firstBtn.textContent = '<<';
	firstBtn.disabled = currentPage === 0;
	firstBtn.onclick = () => {
		currentPage = 0;
		if (isFilterActive) {
			applyFilters();
		} else {
			fetchAllEmployees();
		}
	};
	container.appendChild(firstBtn);
	
	const prevBtn = document.createElement('button');
	prevBtn.className = 'btn btn-outline-secondary mx-1';
	prevBtn.textContent = '<';
	prevBtn.disabled = currentPage === 0;
	prevBtn.onclick = () => {
		if (currentPage > 0) {
			currentPage--;
			if (isFilterActive) {
				applyFilters();
			} else {
				fetchAllEmployees();
			}
		}
	};
	container.appendChild(prevBtn);
	
	let maxPageButtons = 5;
	let startPage = Math.max(0, currentPage - Math.floor(maxPageButtons / 2));
	let endPage = startPage + maxPageButtons - 1;

	if (endPage >= totalPages) {
		endPage = totalPages - 1;
		startPage = Math.max(0, endPage - maxPageButtons + 1);
	}

	for (let i = startPage; i <= endPage; i++) {
		const btn = document.createElement('button');
		btn.className = `btn mx-1 ${i === currentPage ? 'btn-secondary' : 'btn-outline-secondary'}`;
		btn.textContent = i + 1;
		btn.onclick = () => {
			currentPage = i;
			if (isFilterActive) {
				applyFilters();
			} else {
				fetchAllEmployees();
			}
		};
		container.appendChild(btn);
	}
	
	const nextBtn = document.createElement('button');
	nextBtn.className = 'btn btn-outline-secondary mx-1';
	nextBtn.textContent = '>';
	nextBtn.disabled = currentPage === totalPages - 1;
	nextBtn.onclick = () => {
		if (currentPage < totalPages - 1) {
			currentPage++;
			if (isFilterActive) {
				applyFilters();
			} else {
				fetchAllEmployees();
			}
		}
	};
	container.appendChild(nextBtn);
	
	const lastBtn = document.createElement('button');
	lastBtn.className = 'btn btn-outline-secondary mx-1';
	lastBtn.textContent = '>>';
	lastBtn.disabled = currentPage === totalPages - 1;
	lastBtn.onclick = () => {
		currentPage = totalPages - 1;
		if (isFilterActive) {
			applyFilters();
		} else {
			fetchAllEmployees();
		}
	};
	container.appendChild(lastBtn);
}


async function fetchDepartments(containerId, selectedValue) {
	let hasSelected = false;
	
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
			
			if (department.name === selectedValue) {
				option.selected = true;
				hasSelected = true;
			}
			
			departmentSelect.appendChild(option);
		});
	} catch (error) {
		console.error('Error fetching departments: ', error);
	}
}

async function fetchAllEmployees() {
	try {
		const params = new URLSearchParams({
			page: currentPage,
			size: pageSize,
		});

		if (sortField && sortDirection) {
			params.append('sort', `${sortField},${sortDirection}`);
		}

		const response = await fetch(`/employees/all?${params.toString()}`);
		const result = await response.json();
		
		if (!result.success) {
			showErrorAlert(result.message, 5000);
		}

		if (result.isEmpty) {
			showWarningAlert(result.message, 4000);
		}
		
		displayHome(result.noOfEmployees, result.averageSalary, result.averageAge, result.minAgeRange, result.maxAgeRange, result.employees);
		
		totalPages = result.totalPages;
		renderPagination();
	} catch (error) {
		console.error('Error fetching employees: ', error);
	}
}

async function fetchEmployeeId() {
	const container = document.getElementById('paginationContainer');
	container.innerHTML = '';
	isFilterActive = false;
	
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
	fetchDepartments('departmentSelect', 'ALL DEPARTMENTS');
	
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
		
		displayHome(result.noOfEmployees, result.averageSalary, result.averageAge, result.minAgeRange, result.maxAgeRange, result.employees);
	} catch (error) {
		console.error('Error fetching employee by ID: ', error);
	}
}

async function applyFilters() {
	const searchIdInput = document.getElementById('searchIdInput');
	searchIdInput.value = '';
	
	isFilterActive = true;
	
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
			
			departmentId = departmentResult.departmentId;
		} catch (error) {
			console.error('Error fetching department ID: ', error);
		}
	}
	
	const params = new URLSearchParams({
		name: employeeName,
		departmentId: departmentId,
		minAge: minAge,
		maxAge: maxAge,
		page: currentPage,
		size: pageSize,
		sort: `${sortField},${sortDirection}`
	});
	
	try {
		console.log("Request URL:", `/employees/filter?${params.toString()}`);
		const response = await fetch(`/employees/filter?${params.toString()}`);
		const result = await response.json();
		
		if (!result.success) {
			showErrorAlert(result.message, 5000);
		}
		
		if (result.isEmpty) {
			showWarningAlert(result.message, 4000);
		}
		
		displayHome(result.noOfEmployees, result.averageSalary, result.averageAge, result.minAgeRange, result.maxAgeRange, result.employees);
		
		totalPages = result.totalPages;
		renderPagination();
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
	fetchDepartments('departmentSelect', 'ALL DEPARTMENTS');
	
	const minAgeInput = document.getElementById('minAgeInput');
	minAgeInput.value = '';
	
	const maxAgeInput = document.getElementById('maxAgeInput');
	maxAgeInput.value = '';
	
	currentPage = 0;
	isFilterActive = false;
	let sortField = 'id';
	let sortDirection = 'asc';
	fetchAllEmployees();
}
