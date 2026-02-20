@Entity
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    private String password;

    private String role;

    private String kycStatus = "Pending";

    private Double budget;
    private String interest;
    private String sector;
    private Double fundRequired;

    private String otp;
    private LocalDateTime otpExpiry;
    private Boolean verified = false;
}
