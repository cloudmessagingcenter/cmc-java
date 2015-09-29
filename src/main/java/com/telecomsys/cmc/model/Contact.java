package com.telecomsys.cmc.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contact model.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contact {

    /**
     * Cell Number.
     */
    @JsonProperty("mdn")
    private String cellNumber;

    /**
     * First Name.
     */
    @JsonProperty("first")
    private String firstName;

    /**
     * Last Name.
     */
    @JsonProperty("last")
    private String lastName;

    /**
     * Email Address.
     */
    @JsonProperty("email")
    private String emailAddress;

    /**
     * Company.
     */
    @JsonProperty("org")
    private String organization;

    /**
     * Title.
     */
    @JsonProperty("title")
    private String title;

    /**
     * City.
     */
    @JsonProperty("city")
    private String city;

    /**
     * State.
     */
    @JsonProperty("state")
    private String state;

    /**
     * Zip Code.
     */
    @JsonProperty("postalcode")
    private String postalCode;

    /**
     * Country code.
     */
    @JsonProperty("country")
    private String countryCode;

    /**
     * Constructor with mandatory parameters.
     *
     * @param cellNumber contact's MDN or cell number.
     * @param firstname contact's first name.
     * @param lastName contact's last name.
     */
    public Contact(String cellNumber, String firstname, String lastName) {
        this.cellNumber = cellNumber;
        this.firstName = firstname;
        this.lastName = lastName;
    }

    /**
     * Default constructor.
     */
    public Contact() {
    }

    /**
     * @return the cellNumber
     */
    public String getCellNumber() {
        return cellNumber;
    }

    /**
     * @param cellNumber the cellNumber to set
     */
    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the organization
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * @param organization the organization to set
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the countryCode
     */
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * @param countryCode the countryCode to set
     */
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

}
