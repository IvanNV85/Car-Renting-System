package fu.carrentingsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void testShowLoginForm() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testShowRegisterForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerRequest"));
    }

    @Test
    void testRegisterWithMismatchedPassword() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("accountName", "testuser")
                        .param("email", "testuser@gmail.com")
                        .param("password", "123456")
                        .param("confirmPassword", "different")
                        .param("fullName", "Nguyen Van A")
                        .param("mobile", "0987654321")
                        .param("birthday", "2000-01-01")
                        .param("identityCard", "012345678901")
                        .param("licenceNumber", "GPLX123456")
                        .param("licenceDate", "2020-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testRegisterWithEmptyFields() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().hasErrors());
    }

    @Test
    void testRegisterSuccess() throws Exception {
        long time = System.currentTimeMillis();
        String suffix = String.valueOf(time % 1000000);
        String mobile = "098" + String.format("%07d", time % 10000000);
        String idCard = "001" + String.format("%09d", time % 1000000000);
        String licence = "GPLX" + String.format("%08d", time % 100000000);

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("accountName", "user" + suffix)
                        .param("email", "user" + suffix + "@gmail.com")
                        .param("password", "123456")
                        .param("confirmPassword", "123456")
                        .param("fullName", "Nguyen Van " + suffix)
                        .param("mobile", mobile)
                        .param("birthday", "1998-05-15")
                        .param("identityCard", idCard)
                        .param("licenceNumber", licence)
                        .param("licenceDate", "2021-06-20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successMessage"));
    }
}
