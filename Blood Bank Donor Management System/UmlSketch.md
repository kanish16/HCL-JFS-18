classDiagram
    class BaseEntity {
        <<abstract>>
        -String id
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +getId() String
        +getCreatedAt() LocalDateTime
        +getUpdatedAt() LocalDateTime
    }

    class AppUser {
        <<abstract>>
        -String username
        -String email
        -String role
        +getLandingModule() String
        +getUsername() String
        +getRole() String
    }

    class AdminUser {
        +getLandingModule() String
    }

    class StaffUser {
        +getLandingModule() String
    }

    class HospitalUser {
        -String hospitalName
        +getLandingModule() String
    }

    class DonorUser {
        -String donorReferenceId
        +getLandingModule() String
    }

    class CompatibilityStrategy {
        <<interface>>
        +getCompatibleDonorGroups(String recipientGroup) Set~String~
        +getComponentScope() String
    }

    class RedBloodCellCompatibilityStrategy {
        -Map RBC_MAP
        +getCompatibleDonorGroups(String) Set~String~
        +getComponentScope() String
    }

    class PlasmaCompatibilityStrategy {
        -Map PLASMA_MAP
        +getCompatibleDonorGroups(String) Set~String~
        +getComponentScope() String
    }

    BaseEntity <|-- AppUser
    AppUser <|-- AdminUser
    AppUser <|-- StaffUser
    AppUser <|-- HospitalUser
    AppUser <|-- DonorUser

    CompatibilityStrategy <|.. RedBloodCellCompatibilityStrategy
    CompatibilityStrategy <|.. PlasmaCompatibilityStrategy
