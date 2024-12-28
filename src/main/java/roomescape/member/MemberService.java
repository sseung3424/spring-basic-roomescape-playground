package roomescape.member;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.stereotype.Service;

@Service
public class MemberService {
    private MemberDao memberDao;

    public MemberService(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        Member member = memberDao.save(new Member(memberRequest.getName(), memberRequest.getEmail(), memberRequest.getPassword(), "USER"));
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public MemberResponse findMember(MemberRequest memberRequest) {
        Member member = memberDao.findByEmailAndPassword(memberRequest.getEmail(), memberRequest.getPassword());
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public String createToken(String email, String password) {
        String secret = "roomescape-application-secret-key-for-login!";
        Key key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        Member member = memberDao.findByEmailAndPassword(email, password);
        return Jwts.builder()
            .setSubject("user-identifier")
            .claim("name", member.getName())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    public MemberResponse findMemberByToken(String token) {
        String secret = "roomescape-application-secret-key-for-login!";
        Key key = new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());

        Claims claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();

        String name = claims.get("name", String.class);
        Member member = memberDao.findByName(name);
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }
}
