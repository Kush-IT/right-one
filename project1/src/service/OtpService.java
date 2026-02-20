@Service
public class OtpService {

    @Autowired
    private JavaMailSender sender;

    public void sendOtp(String email,String otp){

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("RIGHT ONE OTP");
        message.setText("Your OTP is: "+otp);

        sender.send(message);
    }
}
