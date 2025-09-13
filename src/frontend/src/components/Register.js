import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { register as registerService } from "../services/authService";
import { AuthContext } from "../context/AuthContext";
import "./Login.css";

const port = process.env.REACT_APP_BACKEND_PORT;
const BASE_URL = `http://localhost:${port}/api`;
const registerSteps = [
  "name",
  "birth",
  "username",
  "password",
  "phone",
  "profile",
];

function Register() {
  const [step, setStep] = useState(0);
  const [data, setData] = useState({
    firstName: "",
    lastName: "",
    birthDate: "",
    gender: "",
    username: "",
    password: "",
    confirm: "",
    phone: "",
    profilePic: null,
  });
  const [errors, setErrors] = useState({});
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const { login } = useContext(AuthContext);

  const validate = async () => {
    const errs = {};
    switch (registerSteps[step]) {
      case "name":
        if (!data.firstName.trim()) {
          errs.firstName = "First name is required";
        } else if (!/^[A-Za-z]+$/.test(data.firstName)) {
          errs.firstName = "First name must contain only English letters";
        }

        if (!data.lastName.trim()) {
          errs.lastName = "Last name is required";
        } else if (!/^[A-Za-z]+$/.test(data.lastName)) {
          errs.lastName = "Last name must contain only English letters";
        }
        break;

      case "birth":
        if (!data.birthDate) {
          errs.birthDate = "Birth date is required";
        } else if (new Date(data.birthDate) > new Date()) {
          errs.birthDate = "Birth date must be in the past";
        }

        if (!data.gender) {
          errs.gender = "Gender is required";
        }
        break;

      case "username":
        if (!data.username.trim()) {
          errs.username = "Username is required";
        } else if (data.username.length < 3) {
          errs.username = "Username must be at least 3 characters";
        } else if (!/^[a-zA-Z0-9]+$/.test(data.username)) {
          errs.username =
            "Username must contain only English letters and numbers";
        } else {
          try {
            const response = await fetch(
              `${BASE_URL}/user/username/${data.username}`
            );
            const result = await response.json();
            if (result.exists) {
              errs.username =
                "Username already exists. Please choose a different one";
            }
          } catch (err) {
            errs.username = "Failed to validate username. Please try again.";
          }
        }
        break;

      case "password":
        if (!data.password) {
          errs.password = "Password is required";
        } else if (data.password.length < 6) {
          errs.password = "Password must be at least 6 characters";
        } else if (/\s/.test(data.password)) {
          errs.password = "Password cannot contain spaces";
        }

        if (!data.confirm) {
          errs.confirm = "Please confirm your password";
        } else if (data.password !== data.confirm) {
          errs.confirm = "Passwords must match";
        }
        break;

      case "phone":
        if (!data.phone) {
          errs.phone = "Phone number is required";
        } else if (!/^05\d{8}$/.test(data.phone)) {
          errs.phone =
            "Phone number must be 10 digits long and start with '05'";
        }
        break;

      case "profile":
        // Profile picture is now optional - no validation needed
        if (
          data.profilePic &&
          !/\.(jpg|jpeg|png|gif)$/i.test(data.profilePic.name)
        ) {
          errs.profilePic =
            "Please upload a valid image file (jpg, jpeg, png, gif)";
        }
        break;

      default:
    }
    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  const handleNext = async () => {
    if (!(await validate())) return;

    if (step < registerSteps.length - 1) {
      setStep(step + 1);
    } else {
      const formData = new FormData();
      formData.append("firstName", data.firstName);
      formData.append("lastName", data.lastName);
      formData.append("username", data.username);
      formData.append("password", data.password);
      formData.append("phoneNumber", data.phone);
      formData.append("birthDate", data.birthDate);
      formData.append("gender", data.gender);

      // Only append profile picture if one is selected
      if (data.profilePic) {
        formData.append("profilePic", data.profilePic);
      }

      try {
        const res = await registerService(formData);
        if (res.error) {
          setErrors({ general: res.error });
          return;
        }
        navigate("/login");
      } catch (err) {
        setErrors({ general: "Network error: " + err.message });
      }
    }
  };

  const handleBack = () => {
    if (step > 0) {
      setStep(step - 1);
    }
  };

  const renderStep = () => {
    switch (registerSteps[step]) {
      case "name":
        return (
          <>
            <input
              type="text"
              placeholder="First Name"
              value={data.firstName}
              onChange={(e) => setData({ ...data, firstName: e.target.value })}
            />
            {errors.firstName && (
              <p className="error-text">{errors.firstName}</p>
            )}

            <input
              type="text"
              placeholder="Last Name"
              value={data.lastName}
              onChange={(e) => setData({ ...data, lastName: e.target.value })}
            />
            {errors.lastName && <p className="error-text">{errors.lastName}</p>}
          </>
        );

      case "birth":
        return (
          <>
            <input
              type="date"
              value={data.birthDate}
              onChange={(e) => setData({ ...data, birthDate: e.target.value })}
            />
            {errors.birthDate && (
              <p className="error-text">{errors.birthDate}</p>
            )}

            <select
              value={data.gender}
              onChange={(e) => setData({ ...data, gender: e.target.value })}
            >
              <option value="">Select Gender</option>
              <option value="female">Female</option>
              <option value="male">Male</option>
              <option value="other">Other</option>
            </select>
            {errors.gender && <p className="error-text">{errors.gender}</p>}
          </>
        );

      case "username":
        return (
          <>
            <input
              type="text"
              placeholder="Username"
              value={data.username}
              onChange={(e) => setData({ ...data, username: e.target.value })}
            />
            {errors.username && <p className="error-text">{errors.username}</p>}
          </>
        );

      case "password":
        return (
          <>
            <div className="password-group">
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={data.password}
                onChange={(e) => setData({ ...data, password: e.target.value })}
              />
              <input
                type={showPassword ? "text" : "password"}
                placeholder="Confirm Password"
                value={data.confirm}
                onChange={(e) => setData({ ...data, confirm: e.target.value })}
              />
              <button
                type="button"
                className="show-hide"
                onClick={() => setShowPassword((prev) => !prev)}
              >
                {showPassword ? "Hide" : "Show"}
              </button>
            </div>
            {errors.password && <p className="error-text">{errors.password}</p>}
            {errors.confirm && <p className="error-text">{errors.confirm}</p>}
          </>
        );

      case "phone":
        return (
          <>
            <input
              type="tel"
              placeholder="Phone Number (05XXXXXXXX)"
              value={data.phone}
              onChange={(e) => setData({ ...data, phone: e.target.value })}
            />
            {errors.phone && <p className="error-text">{errors.phone}</p>}
          </>
        );

      case "profile":
        return (
          <>
            <p className="profile-hint">
              Profile picture is optional. You can skip this step.
            </p>
            <input
              type="file"
              accept="image/*"
              onChange={(e) =>
                setData({ ...data, profilePic: e.target.files[0] })
              }
            />
            {data.profilePic && (
              <p className="profile-selected">
                Selected: {data.profilePic.name}
              </p>
            )}
            {errors.profilePic && (
              <p className="error-text">{errors.profilePic}</p>
            )}
            {errors.general && <p className="error-text">{errors.general}</p>}
          </>
        );

      default:
        return null;
    }
  };

  const getStepTitle = () => {
    switch (registerSteps[step]) {
      case "name":
        return "Enter Your Name";
      case "birth":
        return "Birth Date & Gender";
      case "username":
        return "Choose Username";
      case "password":
        return "Create Password";
      case "phone":
        return "Phone Number";
      case "profile":
        return "Profile Picture (Optional)";
      default:
        return "Register";
    }
  };

  return (
    <div className="auth-wrapper register-wrapper">
      <div
        className="auth-slider register-slider"
        style={{
          transform: `translateX(-${step * (100 / registerSteps.length)}%)`,
        }}
      >
        {registerSteps.map((_, idx) => (
          <div className="auth-panel" key={idx}>
            {idx === step && (
              <form
                onSubmit={(e) => {
                  e.preventDefault();
                  handleNext();
                }}
              >
                <h2>{getStepTitle()}</h2>
                {/* <div className="register-heading">
                  <h1>Sign in</h1>
                  <p>to continue to Email</p>
                </div> */}

                {renderStep()}
                <div className="button-row">
                  {step > 0 && (
                    <button type="button" onClick={handleBack}>
                      Back
                    </button>
                  )}
                  <button type="submit">
                    {step === registerSteps.length - 1 ? "Sign Up" : "Next"}
                  </button>
                </div>
              </form>
            )}
          </div>
        ))}
      </div>
    </div>
  );
}

export default Register;
