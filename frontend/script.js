// Employment Payroll System - Shared JavaScript Functions

// API Configuration
const API_BASE_URL = 'http://localhost:8080';

// Authentication Functions
function checkAuthentication() {
    const isLoggedIn = localStorage.getItem('isLoggedIn');
    if (isLoggedIn !== 'true') {
        window.location.href = 'index.html';
        return false;
    }
    return true;
}

function logout() {
    // Clear authentication data
    localStorage.removeItem('isLoggedIn');
    localStorage.removeItem('username');
    
    // Redirect to login page
    window.location.href = 'index.html';
}

// API Helper Functions
async function apiRequest(endpoint, options = {}) {
    const url = `${API_BASE_URL}${endpoint}`;
    
    const defaultOptions = {
        method: 'GET',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
    };
    
    const config = { ...defaultOptions, ...options };
    
    try {
        const response = await fetch(url, config);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        } else {
            return await response.text();
        }
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Employee API Functions
const EmployeeAPI = {
    async getAll() {
        return await apiRequest('/employees');
    },
    
    async getById(id) {
        return await apiRequest(`/employee/${id}`);
    },
    
    async create(employeeData) {
        const formData = new URLSearchParams();
        Object.keys(employeeData).forEach(key => {
            formData.append(key, employeeData[key]);
        });
        
        return await apiRequest('/employees', {
            method: 'POST',
            body: formData
        });
    },
    
    async update(id, employeeData) {
        const formData = new URLSearchParams();
        Object.keys(employeeData).forEach(key => {
            formData.append(key, employeeData[key]);
        });
        
        return await apiRequest(`/employee/${id}`, {
            method: 'PUT',
            body: formData
        });
    },
    
    async delete(id) {
        return await apiRequest(`/employee/${id}`, {
            method: 'DELETE'
        });
    }
};

// Payroll API Functions
const PayrollAPI = {
    async getAll() {
        return await apiRequest('/payroll');
    },
    
    async calculate(payrollData) {
        const formData = new URLSearchParams();
        Object.keys(payrollData).forEach(key => {
            formData.append(key, payrollData[key]);
        });
        
        return await apiRequest('/payroll/calculate', {
            method: 'POST',
            body: formData
        });
    }
};

// Admin API Functions
const AdminAPI = {
    async login(username, password) {
        const formData = new URLSearchParams();
        formData.append('username', username);
        formData.append('password', password);
        
        return await apiRequest('/login', {
            method: 'POST',
            body: formData
        });
    }
};

// Utility Functions
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD'
    }).format(amount);
}

function formatDate(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

function formatDateTime(dateString) {
    if (!dateString) return 'N/A';
    const date = new Date(dateString);
    return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Validation Functions
function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function validatePhoneNumber(phone) {
    const phoneRegex = /^\+?[\d\s\-\(\)]+$/;
    return phoneRegex.test(phone) && phone.replace(/\D/g, '').length >= 10;
}

function validateSalary(salary) {
    const amount = parseFloat(salary);
    return !isNaN(amount) && amount >= 0 && amount <= 1000000;
}

function validateRequired(value) {
    return value !== null && value !== undefined && value.toString().trim() !== '';
}

// Form Validation Helper
function validateForm(formElement, rules) {
    const errors = [];
    const formData = new FormData(formElement);
    
    for (const [fieldName, fieldRules] of Object.entries(rules)) {
        const value = formData.get(fieldName);
        
        for (const rule of fieldRules) {
            if (rule.type === 'required' && !validateRequired(value)) {
                errors.push(`${rule.field || fieldName} is required`);
            } else if (rule.type === 'email' && value && !validateEmail(value)) {
                errors.push(`${rule.field || fieldName} must be a valid email`);
            } else if (rule.type === 'phone' && value && !validatePhoneNumber(value)) {
                errors.push(`${rule.field || fieldName} must be a valid phone number`);
            } else if (rule.type === 'salary' && value && !validateSalary(value)) {
                errors.push(`${rule.field || fieldName} must be a valid salary amount`);
            } else if (rule.type === 'min' && value && parseFloat(value) < rule.value) {
                errors.push(`${rule.field || fieldName} must be at least ${rule.value}`);
            } else if (rule.type === 'max' && value && parseFloat(value) > rule.value) {
                errors.push(`${rule.field || fieldName} must be at most ${rule.value}`);
            }
        }
    }
    
    return {
        isValid: errors.length === 0,
        errors: errors
    };
}

// UI Helper Functions
function showNotification(message, type = 'info', duration = 5000) {
    // Remove existing notifications
    const existingNotification = document.querySelector('.notification');
    if (existingNotification) {
        existingNotification.remove();
    }
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        border-radius: 5px;
        box-shadow: 0 5px 15px rgba(0,0,0,0.1);
        z-index: 1100;
        max-width: 400px;
        font-weight: 500;
        animation: slideInRight 0.3s ease;
    `;
    
    // Set notification styles based on type
    switch (type) {
        case 'success':
            notification.style.backgroundColor = '#d4edda';
            notification.style.color = '#155724';
            notification.style.borderLeft = '4px solid #28a745';
            break;
        case 'error':
            notification.style.backgroundColor = '#f8d7da';
            notification.style.color = '#721c24';
            notification.style.borderLeft = '4px solid #dc3545';
            break;
        case 'warning':
            notification.style.backgroundColor = '#fff3cd';
            notification.style.color = '#856404';
            notification.style.borderLeft = '4px solid #ffc107';
            break;
        default:
            notification.style.backgroundColor = '#d1ecf1';
            notification.style.color = '#0c5460';
            notification.style.borderLeft = '4px solid #17a2b8';
    }
    
    notification.textContent = message;
    document.body.appendChild(notification);
    
    // Auto-remove notification
    setTimeout(() => {
        if (notification.parentNode) {
            notification.style.animation = 'slideOutRight 0.3s ease';
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.remove();
                }
            }, 300);
        }
    }, duration);
}

// Loading Spinner Helper
function showLoadingSpinner(element, show = true) {
    if (show) {
        element.style.display = 'flex';
        element.innerHTML = `
            <div style="text-align: center; padding: 3rem;">
                <div class="spinner" style="
                    border: 4px solid #f3f3f3;
                    border-top: 4px solid #667eea;
                    border-radius: 50%;
                    width: 40px;
                    height: 40px;
                    animation: spin 1s linear infinite;
                    margin: 0 auto 1rem;
                "></div>
                <p>Loading...</p>
            </div>
        `;
    } else {
        element.style.display = 'none';
    }
}

// Local Storage Helpers
const Storage = {
    set(key, value) {
        try {
            localStorage.setItem(key, JSON.stringify(value));
            return true;
        } catch (error) {
            console.error('Error saving to localStorage:', error);
            return false;
        }
    },
    
    get(key, defaultValue = null) {
        try {
            const item = localStorage.getItem(key);
            return item ? JSON.parse(item) : defaultValue;
        } catch (error) {
            console.error('Error reading from localStorage:', error);
            return defaultValue;
        }
    },
    
    remove(key) {
        try {
            localStorage.removeItem(key);
            return true;
        } catch (error) {
            console.error('Error removing from localStorage:', error);
            return false;
        }
    },
    
    clear() {
        try {
            localStorage.clear();
            return true;
        } catch (error) {
            console.error('Error clearing localStorage:', error);
            return false;
        }
    }
};

// Network Status Helper
function checkNetworkConnection() {
    return navigator.onLine;
}

// Error Handling
function handleError(error, context = 'Operation') {
    console.error(`${context} failed:`, error);
    
    let message = `${context} failed. `;
    
    if (!checkNetworkConnection()) {
        message += 'Please check your internet connection.';
    } else if (error.message.includes('500')) {
        message += 'Server error. Please try again later.';
    } else if (error.message.includes('404')) {
        message += 'Resource not found.';
    } else if (error.message.includes('fetch')) {
        message += 'Unable to connect to server. Please ensure the server is running.';
    } else {
        message += 'Please try again or contact support if the problem persists.';
    }
    
    showNotification(message, 'error');
}

// Debounce Function for Search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Table Helper Functions
function createTableCell(content, className = '') {
    const td = document.createElement('td');
    td.innerHTML = content;
    if (className) {
        td.className = className;
    }
    return td;
}

function createActionButton(text, className, onclick) {
    const button = document.createElement('button');
    button.textContent = text;
    button.className = `btn btn-small ${className}`;
    button.onclick = onclick;
    return button;
}

// Export functions if module system is being used
if (typeof module !== 'undefined' && module.exports) {
    module.exports = {
        checkAuthentication,
        logout,
        EmployeeAPI,
        PayrollAPI,
        AdminAPI,
        formatCurrency,
        formatDate,
        formatDateTime,
        validateForm,
        showNotification,
        showLoadingSpinner,
        Storage,
        handleError,
        debounce
    };
}