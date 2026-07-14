# Form Validation Implementation Guide

## Backend Validation

### 1. DTO Validation (Jakarta Bean Validation)

All request DTOs use Jakarta Validation annotations:

#### Common Annotations
- `@NotNull` - Field must not be null
- `@NotBlank` - String must not be empty after trim
- `@NotEmpty` - Collection/String must have elements
- `@Size(min=x, max=y)` - String length or collection size constraints
- `@Min / @Max` - Numeric value constraints
- `@DecimalMin / @DecimalMax` - BigDecimal constraints
- `@Email` - Valid email format
- `@Pattern(regexp=...)` - Custom regex validation
- `@Valid` - Nested object validation (cascade)

#### Validated DTOs
- `RegisterRequest` - email, name, password validation
- `LoginRequest` - email, password required
- `StoreProductRequest` - product details with price, stock, category
- `UpdateProductRequest` - update product fields
- `PlaceOrderRequest` - shipping address, cart items validation
- `AddToCartRequest` - product ID, quantity validation
- `CategoryRequest` - category name, slug, optional description
- `AddressPayload` - nested validation in order request

### 2. Controller Integration

Enable validation on all controllers:

```java
@PostMapping
public ResponseEntity<ApiResponse<T>> create(
    @Valid @RequestBody YourRequest request
) { ... }
```

The `@Valid` annotation triggers validation. Spring automatically:
1. Validates all constraints
2. Returns 400 Bad Request with validation errors if any constraints fail
3. Populates `BindingResult` for custom error handling

### 3. Global Exception Handler

Spring's `GlobalExceptionHandler` automatically catches `MethodArgumentNotValidException`:

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
    MethodArgumentNotValidException ex
) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );
    return ResponseEntity.badRequest()
        .body(ApiResponse.error("Validation failed", errors));
}
```

## Frontend Validation

### 1. Installation

```bash
npm install react-hook-form
npm install zod @hookform/resolvers  # For schema validation (optional)
```

### 2. Form Validation Component Example

```jsx
import { useForm } from 'react-hook-form';

function MyForm() {
  const { register, handleSubmit, formState: { errors } } = useForm({
    mode: 'onBlur', // Validate on blur
  });

  const onSubmit = async (data) => {
    try {
      const response = await api.post('/api/endpoint', data);
      // Success
    } catch (error) {
      // Handle error
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="form-group">
        <input
          {...register('email', {
            required: 'Email is required',
            pattern: {
              value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
              message: 'Invalid email format'
            }
          })}
        />
        {errors.email && <span className="error">{errors.email.message}</span>}
      </div>

      <button type="submit" disabled={Object.keys(errors).length > 0}>
        Submit
      </button>
    </form>
  );
}
```

### 3. Validation Rules by Field Type

#### Email Fields
```javascript
register('email', {
  required: 'Email is required',
  pattern: {
    value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
    message: 'Invalid email format'
  }
})
```

#### Text Fields (Name, Title, etc.)
```javascript
register('name', {
  required: 'Name is required',
  minLength: { value: 2, message: 'Name must be at least 2 characters' },
  maxLength: { value: 100, message: 'Name must not exceed 100 characters' },
  pattern: { value: /^[a-zA-Z\s]+$/, message: 'Name can only contain letters and spaces' }
})
```

#### Number Fields
```javascript
register('quantity', {
  required: 'Quantity is required',
  min: { value: 1, message: 'Quantity must be at least 1' },
  max: { value: 1000, message: 'Quantity cannot exceed 1000' },
  valueAsNumber: true
})
```

#### Currency/Price Fields
```javascript
register('price', {
  required: 'Price is required',
  min: { value: 0.01, message: 'Price must be positive' },
  pattern: { value: /^\d+(\.\d{1,2})?$/, message: 'Invalid price format' },
  valueAsNumber: true
})
```

#### Password Fields
```javascript
register('password', {
  required: 'Password is required',
  minLength: { value: 8, message: 'Password must be at least 8 characters' },
  pattern: {
    value: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/,
    message: 'Password must contain uppercase, lowercase, and numbers'
  }
})
```

#### URL Fields
```javascript
register('imageUrl', {
  pattern: {
    value: /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w \.-]*)*\/?$/,
    message: 'Invalid URL format'
  }
})
```

### 4. Async Validation

```javascript
register('email', {
  required: 'Email is required',
  validate: async (value) => {
    const response = await api.get(`/auth/check-email?email=${value}`);
    return response.data.available || 'Email is already registered';
  }
})
```

### 5. Conditional Validation

```javascript
watch() function to access field values:

function MyForm() {
  const { register, watch, formState: { errors } } = useForm();
  const passwordValue = watch('password');

  return (
    <>
      <input {...register('password', { required: 'Password is required' })} />
      <input
        {...register('confirmPassword', {
          required: 'Confirm password is required',
          validate: (value) => value === passwordValue || 'Passwords do not match'
        })}
      />
    </>
  );
}
```

## Error Handling & Display

### Backend Error Response Format

```json
{
  "success": false,
  "message": "Validation failed",
  "data": {
    "email": "Invalid email format",
    "password": "Password must be at least 8 characters",
    "name": "Name is required"
  }
}
```

### Frontend Error Display

```jsx
function FormField({ label, error, ...props }) {
  return (
    <div className="form-group">
      <label>{label}</label>
      <input {...props} className={error ? 'input-error' : ''} />
      {error && <span className="field-error">{error.message}</span>}
    </div>
  );
}
```

### CSS for Error States

```css
.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  font-weight: 500;
  margin-bottom: 4px;
  color: #111827;
}

.form-group input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
  font-size: 14px;
}

.form-group input.input-error {
  border-color: #dc3545;
  background-color: #fff5f5;
}

.field-error {
  display: block;
  color: #dc3545;
  font-size: 12px;
  margin-top: 4px;
}

.form-group input:focus {
  outline: none;
  border-color: #0066cc;
  box-shadow: 0 0 0 3px rgba(0, 102, 204, 0.1);
}
```

## Best Practices

1. **Frontend First** - Validate on client before sending to server
2. **Server-Side Always** - Never trust client validation alone
3. **Clear Messages** - Display specific, user-friendly error messages
4. **Real-Time Validation** - Validate on blur for better UX
5. **Disable Submit** - Disable form submission while validation fails
6. **API Error Handling** - Handle and display server validation errors
7. **Accessibility** - Use proper ARIA labels and error announcements
8. **Progressive Enhancement** - Work without JavaScript

## Testing Validation

### Backend Tests
```java
@Test
void testInvalidEmail() {
    RegisterRequest request = new RegisterRequest("test name", "invalid-email", "password123");
    // Should fail validation
}
```

### Frontend Tests
```javascript
test('should show error for invalid email', async () => {
  render(<RegisterForm />);
  const emailInput = screen.getByLabelText('Email');
  await userEvent.type(emailInput, 'invalid-email');
  expect(screen.getByText('Invalid email format')).toBeInTheDocument();
});
```
