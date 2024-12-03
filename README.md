Key Components
Class Definition and Annotation

@RestController: Marks the class as a REST controller, allowing it to handle HTTP requests.
@RequestMapping("/api/holidays"): Sets the base URL for all endpoints in this controller to /api/holidays.
Dependencies

FederalHolidayService: The service layer handling business logic related to holidays.
JobLauncher and Job: Used for triggering and managing a Spring Batch job to import data from a CSV file.
Constructor and Field Injection

The FederalHolidayController constructor injects the FederalHolidayService.
The JobLauncher and Csvjob are injected using @Autowired.
Endpoints
1. @GetMapping
/api/holidays

Fetches a list of all holidays using the service's getAllHolidays() method.
Returns a List<Holiday> in JSON format.
/api/holidays/{id}

Fetches a specific holiday by its ID using service.getHolidayById(id).
Returns a ResponseEntity<Holiday> containing the holiday details.
2. @PostMapping
/api/holidays

Adds a new holiday using service.addHoliday(holiday).
Expects a JSON payload representing a Holiday object.
Returns the saved holiday wrapped in a ResponseEntity.
/api/holidays/importCustomers

Triggers the Spring Batch job (Csvjob) to import customer data from a CSV file.
A unique timestamp is added as a job parameter to ensure the job executes even if the same job was previously run.
Uses jobLauncher.run() to start the job and handles exceptions (e.g., job already running or invalid parameters).
3. @PutMapping
/api/holidays/{id}
Updates an existing holiday by ID with new details using service.updateHoliday(id, updatedHoliday).
Returns the updated holiday wrapped in a ResponseEntity.
4. @DeleteMapping
/api/holidays/{id}
Deletes a holiday by ID using service.deleteHoliday(id).
Returns an empty ResponseEntity with 204 No Content status.
Spring Batch Integration
importCsvToDBJob()
Sets up and launches a Spring Batch job using JobLauncher.
The JobParameters include a timestamp (startAt) to avoid conflicts if the same job is triggered multiple times.
Handles common job-related exceptions:
JobExecutionAlreadyRunningException: The job is already running.
JobRestartException: Error while restarting the job.
JobInstanceAlreadyCompleteException: The job instance is marked as completed.
JobParametersInvalidException: Invalid parameters are provided.
Use Case Overview
CRUD Operations: The controller provides full support for creating, reading, updating, and deleting holidays.
Batch Job Execution: Allows triggering a batch job to import holiday-related data from a CSV file into the database.
Potential Improvements
Error Handling:
Use a global exception handler (@ControllerAdvice) for better error reporting and responses (e.g., 404 for non-existent holiday IDs).
Validation:
Add validation for the Holiday object using annotations like @Valid and @RequestBody to ensure data integrity.
Asynchronous Job Execution:
Implement asynchronous job triggering to improve responsiveness when running batch jobs.


Test Scenario for testGetAllHolidays()
Scenario: Fetch all holidays from the repository.
Given: The repository contains a holiday for USA's New Year on January 1, 2025.
When: service.getAllHolidays() is invoked.
Then: The service should return a list containing exactly one holiday.

Test Scenario for testGetHolidayById()
Scenario: Fetch a holiday by its ID.
Given: The repository contains a holiday with ID 1 for USA's New Year on January 1, 2025.
When: service.getHolidayById(1L) is invoked.
Then: The service should return a holiday object with the name "New Year's Day".

Test Scenario for testAddHoliday()
Scenario: Add a new holiday to the repository.
Given: A new holiday "Independence Day" on July 4, 2025, is provided to the service.
When: service.addHoliday() is invoked with the holiday details.
Then: The service should return the saved holiday with a generated ID.

Test Scenario for testUpdateHoliday()
Scenario: Update an existing holiday.
Given:

A holiday with ID 1 for "Labor Day" on September 2, 2025, already exists.
Updated details for the holiday include a new date (September 1, 2025).
When: service.updateHoliday(1L, updatedHoliday) is invoked.
Then: The service should update the holiday's date and return the updated holiday object.
Test Scenario for testDeleteHoliday()
Scenario: Delete a holiday by its ID.
Given: A holiday with ID 1 exists in the repository.
When: service.deleteHoliday(1L) is invoked.
Then: The service should call the repository's deleteById(1L) method exactly once to remove the holiday.