# Hospital Management System - Modern GUI Wireframes & Design

## Design Principles

### Visual Hierarchy
- **Primary Actions**: Large, prominently colored buttons (Material Blue #2196F3)
- **Secondary Actions**: Smaller buttons with outline style
- **Destructive Actions**: Red color (#F44336) for delete/cancel operations
- **Success Actions**: Green color (#4CAF50) for save/complete operations

### Color Scheme
- **Primary**: #2196F3 (Material Blue) - Main navigation, primary buttons
- **Secondary**: #4CAF50 (Material Green) - Success states, availability indicators
- **Accent**: #FF9800 (Material Orange) - Warnings, pending states
- **Error**: #F44336 (Material Red) - Errors, critical alerts
- **Background**: #FAFAFA (Light Grey) - Main background
- **Surface**: #FFFFFF (White) - Cards, panels, forms
- **Text Primary**: #212121 (Dark Grey) - Main text
- **Text Secondary**: #757575 (Medium Grey) - Secondary text

### Typography
- **Headers**: Roboto Bold, 24px-32px
- **Subheaders**: Roboto Medium, 18px-20px
- **Body Text**: Roboto Regular, 14px-16px
- **Captions**: Roboto Regular, 12px-14px

## Main Application Layout

```
┌─────────────────────────────────────────────────────────────────┐
│ [🏥] Hospital Management System    [🔔] [👤] Admin    [⚙️] [📤] │
├─────────────────────────────────────────────────────────────────┤
│ 🏠 Dashboard │                                                  │
│ 👥 Patients  │              MAIN CONTENT AREA                   │
│ 👨‍⚕️ Doctors   │                                                  │
│ 🏨 Rooms      │         (Dynamic content based on              │
│ 💰 Billing    │          selected navigation item)              │
│ 📊 Reports    │                                                  │
│ ⚙️ Settings   │                                                  │
│               │                                                  │
│               │                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## 1. Dashboard Wireframe

```
┌─────────────────────────────────────────────────────────────────┐
│ Dashboard                                    Today: March 15, 2024│
├─────────────────────────────────────────────────────────────────┤
│ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐     │
│ │   45    │ │   12    │ │   28    │ │  98%    │ │ $45,230 │     │
│ │Patients │ │Doctors  │ │ Rooms   │ │Occupancy│ │ Revenue │     │
│ │ Today   │ │Available│ │Available│ │  Rate   │ │ Today   │     │
│ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘     │
├─────────────────────────────────────────────────────────────────┤
│ Recent Activities                    │ Pending Tasks             │
│ ┌─────────────────────────────────┐  │ ┌─────────────────────────┐│
│ │ 🔴 Emergency: John Doe admitted │  │ │ ⚠️  5 Bills pending     ││
│ │ ✅ Room 203 cleaned & ready    │  │ │ 📋 3 Discharge reviews  ││
│ │ 💊 Medication restocked         │  │ │ 📞 2 Insurance calls    ││
│ │ 👨‍⚕️ Dr. Smith arrived           │  │ │ 📧 7 Unread messages    ││
│ └─────────────────────────────────┘  │ └─────────────────────────┘│
├─────────────────────────────────────────────────────────────────┤
│ Quick Actions                                                    │
│ [+ New Patient] [🏨 Admit Patient] [💰 Create Bill] [📊 Reports]│
└─────────────────────────────────────────────────────────────────┘
```

## 2. Patient Management Wireframe

```
┌─────────────────────────────────────────────────────────────────┐
│ Patient Management                                               │
├─────────────────────────────────────────────────────────────────┤
│ [🔍 Search...] [🏥 Inpatients] [🚶 Outpatients] [+ New Patient] │
├─────────────────────────────────────────────────────────────────┤
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ ID     │ Name           │ Age │ Room │ Status    │ Action   │ │
│ │────────┼────────────────┼─────┼──────┼───────────┼──────────│ │
│ │ P001   │ John Doe       │ 45  │ 201  │ Admitted  │ [View]   │ │
│ │ P002   │ Jane Smith     │ 32  │ 203  │ Admitted  │ [View]   │ │
│ │ P003   │ Bob Johnson    │ 67  │ ICU1 │ Critical  │ [View]   │ │
│ │ P004   │ Alice Brown    │ 28  │ -    │ Outpatient│ [View]   │ │
│ └─────────────────────────────────────────────────────────────┘ │
├─────────────────────────────────────────────────────────────────┤
│ Selected Patient: John Doe (P001)                               │
│ ┌───────────────────┐ ┌─────────────────────────────────────────┐│
│ │ 📋 Basic Info     │ │ 🏥 Current Admission                    ││
│ │ Name: John Doe    │ │ Room: 201 (General)                     ││
│ │ Age: 45           │ │ Admitted: 2024-03-10                    ││
│ │ Blood: O+         │ │ Doctor: Dr. Smith                       ││
│ │ Phone: +1234567890│ │ Diagnosis: Pneumonia                    ││
│ │ [Edit]            │ │ Status: Stable                          ││
│ └───────────────────┘ │ [💊 Medications] [📋 Notes] [📊 Tests] ││
│                       └─────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
```

## 3. Room Management Wireframe

```
┌─────────────────────────────────────────────────────────────────┐
│ Room Management                                                  │
├─────────────────────────────────────────────────────────────────┤
│ Floor: [All ▼] Type: [All ▼] Status: [All ▼] [🔄 Refresh]      │
├─────────────────────────────────────────────────────────────────┤
│ Floor 1 - General Ward                                          │
│ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐│
│ │ 101 │ │ 102 │ │ 103 │ │ 104 │ │ 105 │ │ 106 │ │ 107 │ │ 108 ││
│ │ 🟢  │ │ 🔴  │ │ 🟡  │ │ 🟢  │ │ 🔴  │ │ 🟢  │ │ 🔴  │ │ 🟡  ││
│ │Empty│ │John │ │Clean│ │Empty│ │Jane │ │Empty│ │Bob  │ │Maint││
│ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘│
│                                                                  │
│ Floor 2 - ICU                                                   │
│ ┌─────┐ ┌─────┐ ┌─────┐ ┌─────┐                                │
│ │ICU1 │ │ICU2 │ │ICU3 │ │ICU4 │                                │
│ │ 🔴  │ │ 🔴  │ │ 🟢  │ │ 🟡  │                                │
│ │Alice│ │Mark │ │Empty│ │Clean│                                │
│ └─────┘ └─────┘ └─────┘ └─────┘                                │
├─────────────────────────────────────────────────────────────────┤
│ Legend: 🟢 Available  🔴 Occupied  🟡 Maintenance/Cleaning      │
│                                                                  │
│ Selected Room: 102                                              │
│ Patient: John Doe (P001) | Admitted: 2024-03-10                │
│ Rate: $2,000/day | Total Stay: 5 days | Total: $10,000        │
│ [🏥 Discharge] [🔄 Transfer] [🧹 Schedule Cleaning]              │
└─────────────────────────────────────────────────────────────────┘
```

## 4. Billing Interface Wireframe

```
┌─────────────────────────────────────────────────────────────────┐
│ Billing & Finance                                               │
├─────────────────────────────────────────────────────────────────┤
│ [📄 New Bill] [💳 Payments] [📊 Reports] [⚙️ Settings]          │
├─────────────────────────────────────────────────────────────────┤
│ Create New Bill                                                 │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Patient: [John Doe (P001)        ▼] [🔍]                   │ │
│ │ Admission: [ADM-2024-001 (Current) ▼]                      │ │
│ │ Bill Date: [2024-03-15] Due Date: [2024-03-30]             │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                  │
│ Bill Items                                                      │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Service/Item        │ Qty │ Rate    │ Amount    │ [Action]  │ │
│ │────────────────────┼─────┼─────────┼───────────┼───────────│ │
│ │ Room Charges (5d)  │  5  │ $2000   │ $10,000   │ [✏️] [❌] │ │
│ │ Doctor Consultation│  3  │ $500    │ $1,500    │ [✏️] [❌] │ │
│ │ Blood Test         │  1  │ $300    │ $300      │ [✏️] [❌] │ │
│ │ X-Ray Chest        │  1  │ $400    │ $400      │ [✏️] [❌] │ │
│ │ Medications        │  1  │ $800    │ $800      │ [✏️] [❌] │ │
│ └─────────────────────────────────────────────────────────────┘ │
│ [+ Add Service] [+ Add Medicine] [+ Add Equipment]              │
│                                                                  │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │                                   Subtotal:     $13,000    │ │
│ │                                   Tax (18%):    $2,340     │ │
│ │                                   Discount:     -$500      │ │
│ │                                   ═══════════════════════   │ │
│ │                                   TOTAL:        $14,840    │ │
│ └─────────────────────────────────────────────────────────────┘ │
│ [💾 Save Draft] [📄 Generate Bill] [🖨️ Print Preview]           │
└─────────────────────────────────────────────────────────────────┘
```

## 5. Modern Login Interface

```
┌─────────────────────────────────────────────────────────────────┐
│                                                                  │
│                    🏥 Hospital Management                        │
│                         System                                  │
│                                                                  │
│     ┌─────────────────────────────────────────────────────┐     │
│     │                                                     │     │
│     │  Welcome Back                                       │     │
│     │                                                     │     │
│     │  Username: [____________________]                   │     │
│     │            👤                                       │     │
│     │                                                     │     │
│     │  Password: [____________________]                   │     │
│     │            🔒                    [👁️]              │     │
│     │                                                     │     │
│     │  [x] Remember me    [Forgot Password?]              │     │
│     │                                                     │     │
│     │          [🔐 Sign In]                              │     │
│     │                                                     │     │
│     │  ──────────── or ────────────                      │     │
│     │                                                     │     │
│     │  [📱 Login with Mobile] [💳 ID Card Login]          │     │
│     │                                                     │     │
│     └─────────────────────────────────────────────────────┘     │
│                                                                  │
│     🔒 Secure Login • 🌐 24/7 Support • 📞 Emergency: 911       │
└─────────────────────────────────────────────────────────────────┘
```

## 6. Patient Registration Form

```
┌─────────────────────────────────────────────────────────────────┐
│ New Patient Registration                               [❌ Close] │
├─────────────────────────────────────────────────────────────────┤
│ Personal Information                                             │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ First Name*: [________________] Last Name*: [______________] │ │
│ │                                                             │ │
│ │ Date of Birth*: [📅 MM/DD/YYYY] Age: [Auto-calculated]     │ │
│ │                                                             │ │
│ │ Gender*: (•) Male ( ) Female ( ) Other                     │ │
│ │                                                             │ │
│ │ Blood Group*: [O+ ▼]      Phone*: [+1 (___) ___-____]     │ │
│ │                                                             │ │
│ │ Email: [________________________@_______]                  │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                  │
│ Emergency Contact                                               │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Contact Name*: [____________________]                       │ │
│ │ Relationship: [Spouse ▼]                                   │ │
│ │ Phone*: [+1 (___) ___-____]                               │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                  │
│ Address Information                                             │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Street Address: [_____________________________________]     │ │
│ │ City: [______________] State: [_____] ZIP: [_______]       │ │
│ │ Country: [United States ▼]                                 │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                  │
│ Medical Information                                             │
│ ┌─────────────────────────────────────────────────────────────┐ │
│ │ Known Allergies: [_____________________________________]    │ │
│ │ Current Medications: [_________________________________]    │ │
│ │ Medical History: [_____________________________________]    │ │
│ │                  [_____________________________________]    │ │
│ │ Insurance Provider: [_______________] Policy: [_________]   │ │
│ └─────────────────────────────────────────────────────────────┘ │
│                                                                  │
│ [💾 Save Patient] [🔄 Clear Form] [❌ Cancel]                  │
└─────────────────────────────────────────────────────────────────┘
```

## Design Features

### Responsive Design
- **Desktop**: Full layout with sidebar navigation
- **Tablet**: Collapsible sidebar, optimized form layouts
- **Mobile**: Bottom navigation, stacked forms

### Accessibility Features
- **High Contrast Mode**: Available for visually impaired users
- **Keyboard Navigation**: Full keyboard support with visible focus indicators
- **Screen Reader Support**: Proper ARIA labels and semantic HTML
- **Font Scaling**: Support for 125%, 150%, 200% text scaling

### Interactive Elements
- **Hover States**: Subtle elevation and color changes
- **Loading States**: Skeleton screens and progress indicators
- **Validation**: Real-time form validation with clear error messages
- **Notifications**: Toast notifications for success/error states

### Data Visualization
- **Charts**: Interactive charts for reports using Chart.js
- **Tables**: Sortable, filterable data tables with pagination
- **Status Indicators**: Color-coded status badges and icons
- **Progress Bars**: Visual progress indicators for processes

This modern design focuses on usability, accessibility, and efficiency while maintaining a professional healthcare aesthetic. 