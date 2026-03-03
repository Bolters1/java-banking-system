Design Patterns Used
1. Factory Pattern (AccountFactory)
The Factory design pattern is used for creating various types of accounts such as BusinessAccount, ClassicAccount, and SavingsAccount. Instead of directly instantiating these objects, the AccountFactory class provides a unified interface to create these accounts, allowing for flexibility and easier maintenance in the future.

2. Singleton Pattern (Singleton)
The Singleton design pattern is used in the Singleton class. This class ensures that only one instance of a certain object exists throughout the application, providing global access to that instance. The Singleton class is primarily used to manage shared resources, such as the output and the TransactionManager.

3. Strategy Pattern (Cashback Strategies)
The Strategy design pattern is applied in the calculation of cashback. There are various strategies for calculating cashback, such as NrOfTransactionsStrategy and SpendingThreshold. The CashbackStrategy interface defines the method of calculation, and each specific strategy implements the algorithm based on different conditions like number of transactions or spending thresholds.

4. Command Pattern (Commands)
The Command design pattern is used to encapsulate various actions or commands that the system can perform. Each action, like adding funds, creating a new account, generating a business report, or making payments, is represented as a separate command class. The Command interface is implemented by these specific command classes, allowing for decoupling of the command request from the code that executes the action.
Project Structure
The project is organized into multiple packages that separate the different aspects of the banking system:

main: Contains the main classes to initialize and execute the program, including the Singleton class for global data access.
service: The core package containing the logic for handling various banking operations.
cashback: Contains classes responsible for calculating and managing cashback strategies.
commands: Includes the different commands (actions) that the system can execute, like adding funds, creating accounts, and generating reports.
commerciants: Responsible for handling the interaction with merchants (commerciants).
exchange: Manages currency exchange logic.
manager: Handles system initialization and setup.
split: Contains logic for splitting payments among multiple accounts.
users: Manages user-related classes, including user accounts and associated details.
users.accounts: Houses classes for different types of accounts (business, classic, savings).
users.cards: Handles cards (including one-time cards).