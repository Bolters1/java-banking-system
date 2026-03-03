## Design Patterns

* **Factory Pattern**
  * Implemented via the `AccountFactory` class.
  * Ensures adherence to the Open/Closed Principle (OCP).
  * Centralizes the creation of account objects (`BusinessAccount`, `ClassicAccount`, `SavingsAccount`) instead of instantiating them directly in the core logic.
  * Allows new account types to be added in the future by simply extending the factory, without modifying existing code.

* **Strategy Pattern**
  * Applied in the cashback calculation module.
  * Avoids massive, hard-to-maintain `switch-case` statements, respecting the Single Responsibility Principle (SRP).
  * Contains:
    * `CashbackStrategy` interface that defines the base contract.
    * Concrete classes (`NrOfTransactionsStrategy`, `SpendingThreshold`) that implement specific algorithms.
  * Facilitates easy extension: adding a new cashback rule only requires creating a new class.

* **Command Pattern**
  * Encapsulates various system actions into separate command classes.
  * Implements the `Command` interface for specific actions (e.g., adding funds, creating accounts, generating business reports).
  * Decouples the invoker (the system processing the input) from the receiver (the actual logic altering the bank's state).
  * Makes the codebase highly modular and simplifies adding new user commands.

* **Singleton Pattern**
  * Utilized for the `Singleton` core class.
  * Maintains a single source of truth throughout the application's lifecycle.
  * Ensures global access to shared resources that must not be duplicated, such as:
    * `TransactionManager`
    * Output generation system
  * Prevents data inconsistency across different modules.

---

## Project Structure

* **Core Packages**
  * `main`
    * Contains the entry point of the application.
    * Handles initialization, including the `Singleton` instance for global data access.
  * `manager`
    * Handles the initial system setup.
    * Manages input and output processing.
  * `service`
    * The core package containing the business logic.
    * Connects the different modules of the application.
  * `commands`
    * Includes interfaces and concrete implementations for system actions.

* **Domain Packages**
  * `users`
    * Manages user entities and their associations.
    * Contains sub-packages:
      * `accounts`: Logic specific to business, classic, and savings accounts.
      * `cards`: Logic for handling physical and One-Time Pay cards.
  * `cashback`
    * Contains the interface and implementations for cashback calculation strategies.
  * `commerciants`
    * Manages data and interactions of the system with merchants.
  * `exchange`
    * Responsible for the currency exchange logic.
  * `split`
    * Contains the logic required for splitting payments among multiple accounts.
