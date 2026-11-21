package impl;

import java.util.Objects;
import java.util.Formatter;

public class Record {
    final String city;
    final String street;
    final int house;
    final int floor;

    public Record(String city, String street, int house, int floor) {
        this.city = city;
        this.street = street;
        this.house = house;
        this.floor = floor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Record)) return false;
        Record other = (Record)obj;
        if (!Objects.equals(city, other.city)) return false;
        if (!Objects.equals(street, other.street)) return false;
        if (house != other.house) return false;
        if (floor != other.floor) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, street, house, floor);
    }

    @Override
    public String toString() {
        return new Formatter()
            .format("{city: %s, street: %s, house: %d, floor: %d}",
                    city, street, house, floor)
            .toString();
    }
}
