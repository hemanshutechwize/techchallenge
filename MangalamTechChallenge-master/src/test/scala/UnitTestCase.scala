import org.scalatest.funsuite.AnyFunSuite

class UnitTestCase extends AnyFunSuite {

  test("Api_status_code_200_Incidents") {
    // Parameters
    val TotalPage = "2"
    val PerPage = "2"
    val Location = "IP"
    val Distance = "10"
    val Stolen = "stolen"
    // Test the accident url
    val accident_url = s"https://bikeindex.org:443/api/v3/search?page=${TotalPage}&per_page=${PerPage}&location=${Location}&distance=${Distance}&stolenness=${Stolen}"
    val response = requests.get(accident_url)
    assert(response.statusCode == 200)
  }

  test("Api_status_code_200_Bikers") {
    // Parameters
    val bikers_id = "1453239"

    // Test the bikers url
    val bikers_url = s"https://bikeindex.org:443/api/v3/bikes/${bikers_id}"
    val response = requests.get(bikers_url)
    assert(response.statusCode == 200)
  }
}
