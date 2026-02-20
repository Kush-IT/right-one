@Entity
@Getter
@Setter
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long investorId;
    private Long startupId;

    private Double amount;

    private String status = "Pending";
}
