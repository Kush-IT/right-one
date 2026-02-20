@
        RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public String signup(@RequestBody User user){

        String otp=String.valueOf(new Random().nextInt(900000)+100000);

        user.setPassword(encoder.encode(user.getPassword()));
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
        user.setVerified(false);

        repo.save(user);
        otpService.sendOtp(user.getEmail(),otp);

        return "OTP Sent";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam String email,
                         @RequestParam String otp){

        User user=repo.findByEmail(email).orElseThrow();

        if(user.getOtp().equals(otp) &&
                user.getOtpExpiry().isAfter(LocalDateTime.now())){

            user.setVerified(true);
            user.setOtp(null);
            repo.save(user);
            return "Verified";
        }

        return "Invalid OTP";
    }

    @PostMapping("/login")
    public String login(@RequestBody User req){

        User user=repo.findByEmail(req.getEmail()).orElseThrow();

        if(!encoder.matches(req.getPassword(),user.getPassword()))
            return "Invalid Password";

        if(!user.getVerified())
            return "Email Not Verified";

        return jwtUtil.generateToken(user.getEmail(),user.getRole());
    }
}
