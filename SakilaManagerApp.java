import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract class Entity {
    protected int id; 
    protected Timestamp createdAt;
    protected Timestamp updatedAt;
    protected boolean active;

    public int getId() { return id; }
    public void setActive(boolean active) { this.active = active; }
}

class DatabaseConnection {
    private static Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/sakila";
    private static final String USER = "root";
    private static final String PASS = "runc";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }
}

interface CrudRepository<T extends Entity> {
    void add(T entity);
    T getById(int id);
    List<T> getAll();
    void update(T entity);
    void delete(int id);
}

// Entidades
class Country extends Entity {
    private String country;
    public Country() {}
    public Country(int id, String country) {
        this.id = id;
        this.country = country;
    }
    public String getCountry() { return country; }
}

class City extends Entity {
    private String city;
    private Country country;
    public City() {}
    public City(int id, String city, Country country) {
        this.id = id;
        this.city = city;
        this.country = country;
    }
    public String getCity() { return city; }
    public Country getCountry() { return country; }
}

class Actor extends Entity {
    private String firstName;
    private String lastName;
    public Actor() {}
    public Actor(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}

class Address extends Entity {
    private String address;
    private String district;
    public Address() {}
    public Address(int id, String address, String district) {
        this.id = id;
        this.address = address;
        this.district = district;
    }
    public String getAddress() { return address; }
    public String getDistrict() { return district; }
}

class Film extends Entity {
    private String title;
    private int releaseYear;
    public Film() {}
    public Film(int id, String title, int releaseYear) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
    }
    public String getTitle() { return title; }
    public int getReleaseYear() { return releaseYear; }
}

// Managers
class ActorManager implements CrudRepository<Actor> {
    @Override
    public List<Actor> getAll() {
        List<Actor> actors = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM actor LIMIT 10")) {
            
            while (rs.next()) {
                actors.add(new Actor(
                    rs.getInt("actor_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return actors;
    }
    @Override public void add(Actor entity) {}
    @Override public Actor getById(int id) { return null; }
    @Override public void update(Actor entity) {}
    @Override public void delete(int id) {}
}

class FilmManager implements CrudRepository<Film> {
    @Override
    public List<Film> getAll() {
        List<Film> films = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM film LIMIT 10")) {
            
            while (rs.next()) {
                films.add(new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getInt("release_year")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }
    @Override public void add(Film entity) {}
    @Override public Film getById(int id) { return null; }
    @Override public void update(Film entity) {}
    @Override public void delete(int id) {}
}

class CityManager implements CrudRepository<City> {
    private CountryManager countryManager = new CountryManager();
    @Override
    public List<City> getAll() {
        List<City> cities = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM city LIMIT 10")) {
            
            while (rs.next()) {
                Country country = countryManager.getById(rs.getInt("country_id"));
                cities.add(new City(
                    rs.getInt("city_id"),
                    rs.getString("city"),
                    country
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cities;
    }
    @Override public void add(City entity) {}
    @Override public City getById(int id) { return null; }
    @Override public void update(City entity) {}
    @Override public void delete(int id) {}
}

class CountryManager implements CrudRepository<Country> {
    @Override
    public List<Country> getAll() {
        List<Country> countries = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM country LIMIT 10")) {
            
            while (rs.next()) {
                countries.add(new Country(
                    rs.getInt("country_id"),
                    rs.getString("country")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countries;
    }
    @Override public void add(Country entity) {}
    @Override public Country getById(int id) { return null; }
    @Override public void update(Country entity) {}
    @Override public void delete(int id) {}
}

class AddressManager implements CrudRepository<Address> {
    @Override
    public List<Address> getAll() {
        List<Address> addresses = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM address LIMIT 10")) {
            
            while (rs.next()) {
                addresses.add(new Address(
                    rs.getInt("address_id"),
                    rs.getString("address"),
                    rs.getString("district")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }
    @Override public void add(Address entity) {}
    @Override public Address getById(int id) { return null; }
    @Override public void update(Address entity) {}
    @Override public void delete(int id) {}
}

class RentalManager {
    public List<Film> getRentedFilms() {
        List<Film> films = new ArrayList<>();
        String query = """
            SELECT DISTINCT f.film_id, f.title, f.release_year 
            FROM rental r
            JOIN inventory i ON r.inventory_id = i.inventory_id
            JOIN film f ON i.film_id = f.film_id
            LIMIT 10
            """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                films.add(new Film(
                    rs.getInt("film_id"),
                    rs.getString("title"),
                    rs.getInt("release_year")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return films;
    }
}

class PaymentManager {
    public double getTotalEarnings() {
        double total = 0.0;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT SUM(amount) AS total FROM payment")) {
            
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }
}

public class SakilaManagerApp {
    public static void main(String[] args) {
        ActorManager actorManager = new ActorManager();
        FilmManager filmManager = new FilmManager();
        CityManager cityManager = new CityManager();
        CountryManager countryManager = new CountryManager();
        AddressManager addressManager = new AddressManager();
        RentalManager rentalManager = new RentalManager();
        PaymentManager paymentManager = new PaymentManager();

        try (Scanner scanner = new Scanner(System.in)) {
            int opcion;
            do {
                System.out.println("\n📋 MENÚ DE OPCIONES");
                System.out.println("1. Ver actores");
                System.out.println("2. Ver películas");
                System.out.println("3. Ver ciudades");
                System.out.println("4. Ver países");
                System.out.println("5. Ver calles (direcciones)");
                System.out.println("6. Ver películas rentadas (con año)");
                System.out.println("7. Ver ganancias totales por renta de películas");
                System.out.println("8. Salir");
                System.out.print("Elige una opción: ");
                opcion = scanner.nextInt();
                System.out.println();

                switch (opcion) {
                    case 1:
                        System.out.println("🎭 Lista de actores:");
                        actorManager.getAll().forEach(a -> 
                            System.out.printf("%d - %s %s%n", 
                                a.getId(), 
                                a.getFirstName(), 
                                a.getLastName()));
                        break;

                    case 2:
                        System.out.println("🎬 Lista de películas:");
                        filmManager.getAll().forEach(f -> 
                            System.out.printf("%d - %s (%d)%n", 
                                f.getId(), 
                                f.getTitle(), 
                                f.getReleaseYear()));
                        break;

                    case 3:
                        System.out.println("🏙️ Lista de ciudades:");
                        cityManager.getAll().forEach(c -> 
                            System.out.printf("%d - %s (%s)%n", 
                                c.getId(), 
                                c.getCity(), 
                                c.getCountry().getCountry()));
                        break;

                    case 4:
                        System.out.println("🌍 Lista de países:");
                        countryManager.getAll().forEach(p -> 
                            System.out.printf("%d - %s%n", 
                                p.getId(), 
                                p.getCountry()));
                        break;

                    case 5:
                        System.out.println("🏠 Lista de direcciones:");
                        addressManager.getAll().forEach(a -> 
                            System.out.printf("%d - %s, Distrito: %s%n", 
                                a.getId(), 
                                a.getAddress(), 
                                a.getDistrict()));
                        break;

                    case 6:
                        System.out.println("📽️ Películas rentadas:");
                        rentalManager.getRentedFilms().forEach(f -> 
                            System.out.printf("%d - %s (%d)%n", 
                                f.getId(), 
                                f.getTitle(), 
                                f.getReleaseYear()));
                        break;

                    case 7:
                        System.out.printf("💰 Ganancias totales: $%.2f%n", 
                            paymentManager.getTotalEarnings());
                        break;

                    case 8:
                        System.out.println("👋 Saliendo del programa...");
                        break;

                    default:
                        System.out.println("⚠️ Opción inválida");
                }
            } while (opcion != 8);
        }
    }
}