package hu.okrim.droneprojectmanager.service;

public interface UserService {

    /**
     * Generates a unique 12-digit account number.
     *
     * @return a newly generated unique account number as a Long
     */
    Long generateAccountNumber();

}
