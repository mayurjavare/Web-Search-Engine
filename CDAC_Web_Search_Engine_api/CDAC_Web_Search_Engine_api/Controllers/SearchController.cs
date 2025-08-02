    using CDAC_Web_Search_Engine_api.Model;
    using Microsoft.AspNetCore.Mvc;
    using Microsoft.Extensions.Options;
    using System.Net.Http;
    using System.Net.Http.Headers;
    using System.Runtime.Intrinsics.X86;
    using System.Text;
    using System.Text.Json;
    using System.Text.Json.Serialization;

    namespace CDAC_Web_Search_Engine_api.Controllers;

    [ApiController]
    [Route("api/[controller]")]
    public class SearchController : ControllerBase
    {
        public readonly HttpClient httpClient;
        public readonly string api_key ;
        public SearchController(HttpClient httpClient,IOptions<OpenRouterSettings> settings)
        {
            this.httpClient = httpClient;
            api_key = settings.Value.ApiKey;
        }

        [HttpGet("ask")]
        public async Task<IActionResult> AskGemma([FromQuery] string prompt)
        {

            var requestBody = new { model = "google/gemma-3n-e2b-it:free", messages = new[] { new { role = "user", content = prompt } } };
            var content = new StringContent(JsonSerializer.Serialize(requestBody), Encoding.UTF8, "application/json");

            httpClient.DefaultRequestHeaders.Clear();
            httpClient.DefaultRequestHeaders.Authorization = new System.Net.Http.Headers.AuthenticationHeaderValue("Bearer", api_key);
            httpClient.DefaultRequestHeaders.Add("HTTP-Referer", "http://localhost");
            httpClient.DefaultRequestHeaders.Add("X-Title", "CDAC_Web_Search_Engine_api");


            var response = await httpClient.PostAsync("https://openrouter.ai/api/v1/chat/completions", content);

            if (!response.IsSuccessStatusCode)
            {
                return StatusCode((int)response.StatusCode, "Error Calling Openrouter");
            }

            var resultJson = await response.Content.ReadAsStringAsync();
            using var jsonDoc = JsonDocument.Parse(resultJson);
            var root = jsonDoc.RootElement;
            var result = root.GetProperty("choices")[0].GetProperty("message").GetProperty("content").GetString();
            return Ok(result?.Trim());
        }
    }
