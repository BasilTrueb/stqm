package ch.fhnw.swc.mrs.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Represents a movie.
 */
public class Movie {
    static final int MIN_AGE_RATING_AGE = 0;
    static final int MAX_AGE_RATING_AGE = 18;
    static final int DEFAULT_AGE_RATING = MIN_AGE_RATING_AGE;
    
    /** Exception text: Illegal value for age rating was used. */
    public static final String EXC_AGE_RATING = "Age rating must be in range [0, 18]";
    /** Exception text: Id cannot be changed for movies. */
    public static final String EXC_ID_FIXED = "Id cannot be changed for movies";
    /** Exception text: Title must not be null nor empty. */
    public static final String EXC_MISSING_TITLE = "Title must not be null nor emtpy";
    /** Exception text: Release date must not be null. */
    public static final String EXC_MISSING_RELEASE_DATE = "Release date must not be null";
    /** Exception text: price category must not be null. */
    public static final String EXC_MISSING_PRICE_CATEGORY = "price category must not be null";

    private UUID id;
    private boolean rented = false;
    private String title = "Untitled";
    private LocalDate releaseDate;
    private PriceCategory priceCategory;
    private int ageRating;

    /** Logger used to produce logs. */
    private static Logger log = LogManager.getLogger(Movie.class);

    /**
     * Objects initialized with this constructor are not ready for use. They must be assigned an id!
     * 
     * @param aTitle Title of the movie. Must not be null nor empty.
     * @param aReleaseDate Date when this movie was released. Must not be null.
     * @param aPriceCategory Price category for this movie. Must not be null.
     * @param anAgeRating How old a user must be at least to be allowed to rent this Movie. A value
     *            between [0, 18].
     * @throws IllegalArgumentException in case, any of the parameters are null or title is empty.
     */
    public Movie(String aTitle, LocalDate aReleaseDate, PriceCategory aPriceCategory, int anAgeRating) {
        log.trace("entering Movie(String, Date, PriceCategory, int)");
        initializeMovie(aTitle, aReleaseDate, aPriceCategory, anAgeRating);
        log.trace("exiting Movie(String, Date, PriceCategory, int)");
    }

    /**
     * Objects initialized with this constructor are not ready for use. They must be assigned an id!
     * 
     * @param aTitle Title of the movie. Must not be null nor empty.
     * @param aReleaseDate Date when this movie was released. Must not be null.
     * @param aPriceCategory Name of a price category for this movie. Must not be null.
     * @param anAgeRating How old a user must be at least to be allowed to rent this Movie. A value
     *            between [0, 18].
     * @throws IllegalArgumentException in case, any of the parameters are null or title is empty.
     */
    public Movie(String aTitle, String aReleaseDate, String aPriceCategory, int anAgeRating) {
        LocalDate relDate = LocalDate.parse(aReleaseDate, DateTimeFormatter.ISO_DATE);
        PriceCategory pc = PriceCategory.getPriceCategoryFromId(aPriceCategory);
        initializeMovie(aTitle, relDate, pc, anAgeRating);
    }

    private void initializeMovie(String aTitle, LocalDate aReleaseDate, PriceCategory aPriceCategory, int anAgeRating) {
        setTitle(aTitle);
        setReleaseDate(aReleaseDate);
        setPriceCategory(aPriceCategory);
        setAgeRating(anAgeRating);
    }

    /**
     * @return unique identification number of this Movie.
     * @throws IllegalStateException when trying to retrieve id before it was set.
     */
    public UUID getMovieid() {
        log.trace("in getId");
        return id;
    }

    /**
     * @param anId set an unique identification number for this Movie.
     * @throws IllegalStateException when trying to re-set id.
     */
    public void setMovieid(UUID anId) {
        log.trace("entering setId");
        if (id != null) {
            log.trace("exiting setId throwing IllegalStateException");
            throw new IllegalStateException(EXC_ID_FIXED);
        }
        id = anId;
        log.trace("exiting setId");
    }

    /**
     * @return The title of this Movie.
     */
    public String getTitle() {
        log.trace("in getTitle");
        return title;
    }

    /**
     * @param aTitle set the title of this Movie.
     */
    public void setTitle(String aTitle) {
        log.trace("entering setTitle");
        if (aTitle == null || aTitle.trim().isEmpty()) {
            log.trace("exiting setTitle throwing IllegalArgumentException");
            throw new IllegalArgumentException(EXC_MISSING_TITLE);
        }
        title = aTitle;
        log.trace("exiting setTitle");
    }

    /**
     * @return whether this Movie is rented to a User.
     */
    public boolean isRented() {
        log.trace("in isRented");
        return rented;
    }

    /**
     * @param isRented set the rented status.
     */
    public void setRented(boolean isRented) {
        log.trace("entering setRented");
        rented = isRented;
        log.trace("exiting setRented");
    }

    /**
     * @return the date this Movie was released.
     */
    public LocalDate getReleaseDate() {
        log.trace("in getReleaseDate");
        return releaseDate;
    }

    /**
     * @param aReleaseDate set the date this Movie was released.
     */
    public void setReleaseDate(LocalDate aReleaseDate) {
        log.trace("entering setReleaseDate");
        if (aReleaseDate == null) {
            log.trace("exiting setReleaseDate throwing IllegalStateException");
            throw new IllegalArgumentException(EXC_MISSING_RELEASE_DATE);
        }
        releaseDate = aReleaseDate;
        log.trace("exiting setReleaseDate");
    }

    /**
     * @return PriceCategory of this Movie.
     */
    public PriceCategory getPriceCategory() {
        log.trace("in releaseDateProperty");
        return priceCategory;
    }

    /**
     * @param aPriceCategory set PriceCategory for this Movie.
     */
    public void setPriceCategory(PriceCategory aPriceCategory) {
        log.trace("entering setPriceCategory");
        if (aPriceCategory == null) {
            log.trace("exiting setPriceCategory throwing IllegalArgumentException");
            throw new IllegalArgumentException(EXC_MISSING_PRICE_CATEGORY);
        }
        priceCategory = aPriceCategory;
        log.trace("exiting setPriceCategory");
    }

    /**
     * @return The minimum age for being allowed to rent this movie.
     */
    public int getAgeRating() {
        log.trace("in getAgeRating");
        return ageRating;
    }

    /**
     * @param ageRating The minimum age for being allowed to rent this movie.
     */
    public void setAgeRating(int ageRating) {
        log.trace("entering setAgeRating");
        if (ageRating < 0 || ageRating > 18) {
            log.trace("exiting setAgeRating throwing IllegalArgumentException");
            throw new IllegalArgumentException(EXC_AGE_RATING);
        }
        this.ageRating = ageRating;
        log.trace("exiting setAgeRating");
    }

    @Override
    public int hashCode() {
        log.trace("entering hashCode");
        final int prime = 31;
        int result = prime + getMovieid().hashCode();
        result = prime * result + ((getReleaseDate() == null) ? 0 : getReleaseDate().hashCode());
        result = prime * result + ((getTitle() == null) ? 0 : getTitle().hashCode());
        log.trace("exiting hashCode");
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        log.trace("entering equals");
        if (this == obj) {
            log.trace("exiting equals (objects are the same)");
            return true;
        }

        if ((obj == null) || !(obj instanceof Movie)) {
            log.trace("exiting equals (objects are of different type)");
            return false;
        }

        // cast safe here as we tested the type four lines above.
        log.trace("exiting equals");
        return areAttributesEqual((Movie) obj);
    }

    private boolean areAttributesEqual(final Movie other) {
        log.trace("entering areAttributesEqual");
        if (!getMovieid().equals(other.getMovieid())) {
            log.trace("exiting areAttributesEqual on different id");
            return false;
        }

        if (!getReleaseDate().equals(other.getReleaseDate())) {
            log.trace("exiting areAttributesEqual on different release date");
            return false;
        }

        if (!getTitle().equals(other.getTitle())) {
            log.trace("exiting areAttributesEqual on different title");
            return false;
        }

        if (!getPriceCategory().equals(other.getPriceCategory())) {
            log.trace("exiting areAttributesEqual on different price category");
            return false;
        }

        if (getAgeRating() != other.getAgeRating()) {
            log.trace("exiting areAttributesEqual on different age rating");
            return false;
        }

        log.trace("exiting areAttributesEqual on equal objects");
        return true;
    }

}
