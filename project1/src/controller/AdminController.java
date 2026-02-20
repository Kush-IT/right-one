@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository repo;

    @PutMapping("/approve/{id}")
    public User approve(@PathVariable Long id){
        User user=repo.findById(id).orElseThrow();
        user.setKycStatus("Approved");
        return repo.save(user);
    }
}
