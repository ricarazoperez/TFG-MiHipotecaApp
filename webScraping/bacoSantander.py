from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
import time
import re 
import pandas as pd

path = 'C:\Program Files (x86)\chromedriver.exe'
driver = webdriver.Chrome(path)


driver.get("https://www.bancosantander.es/particulares/hipotecas/simulador-hipoteca")
driver.implicitly_wait(10)

boton_cookies = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "/html/body/div[3]/div[3]/div/div/div[2]/div/div/button")))
boton_cookies.click()

iframe = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "iFrameResizer0")))

driver.switch_to.frame(iframe)



input_precio = WebDriverWait(driver, 20).until(EC.presence_of_element_located((By.ID, "input_SIMULATION.SLIDER_PRICE")))
input_precio.send_keys(Keys.CONTROL + "a")
input_precio.send_keys("500000")

tipo_vivienda =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "radio_button_SIMULATION.MORTGAGE_TYPE0")))
tipo_vivienda.click()

vivienda = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "radio_button_SIMULATION.HOME_STATUS0")))
vivienda.click()


titular = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "radio_button_SIMULATION.MORE_THAN_ONE_HOLDER1")))
titular.click()

input_ingresos = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "/html/body/hdfr-root/div/div/div[1]/hdfr-simulation/section/div/div[6]/div[1]/hdfr-currency-input/div/div/input")))
input_ingresos.send_keys(Keys.CONTROL + "a")
input_ingresos.send_keys("5000")

input_fecha = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "/html/body/hdfr-root/div/div/div[1]/hdfr-simulation/section/div/div[7]/div[1]/hdfr-date-picker/div/div/input")))
input_fecha.send_keys(Keys.CONTROL + "a")
input_fecha.send_keys("30/05/1980")


input_comunidad = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "input[type='text'][aria-autocomplete='list']")))
input_comunidad.send_keys(Keys.CONTROL + "a")
input_comunidad.send_keys("Madrid")
action = ActionChains(driver)
action.send_keys(Keys.ENTER).perform()



boton_calcular = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "button_CALCULA TU CUOTA")))
boton_calcular.click()

# espera a que aparezca el elemento que buscas
try:
    print("Falta algun elemento por seleccionar")
    elemento = WebDriverWait(driver, 10).until(
        EC.presence_of_element_located((By.CSS_SELECTOR, "button[id='button_ACEPTAR'] span"))
    )
    elemento.click()
    titular = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "radio_button_SIMULATION.MORE_THAN_ONE_HOLDER1")))
    titular.click()
    boton_calcular = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "button_CALCULA TU CUOTA")))
    boton_calcular.click()

except:
    # si el elemento no aparece, continúa con la ejecución
    print("Ha pasado por que no falta ningun elemento por seleccionar")
    pass


WebDriverWait(driver,10)


nombre_hipoteca = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "div[class='col-6 pr-1 pr-lg-3 ng-star-inserted'] p[class='h5 pl-2 pl-md-3'] strong")))
nombre_hipoteca = nombre_hipoteca.text
nombre_hipoteca = nombre_hipoteca.replace('\n', ' ')
print("nombre_hipoteca " + nombre_hipoteca) # Hipoteca Variable Bonificada

tin_6_meses = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//strong[contains(text(),'1,65 %')])[1]")))
tin_6_meses =  tin_6_meses.text
print("tin_6_meses " + tin_6_meses)# 1,65 %



# SIN CUMPLIR CONDICIONES

tin_resto_plazo_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//h6)[2]")))
tin_resto_plazo_SC =  tin_resto_plazo_SC.text
print("tin_resto_plazo_SC " + tin_resto_plazo_SC) #Euríbor +1,65 %


tae_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[1]")))
tae_SC =  tae_SC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_SC)
if match:
    tae_SC = match.group(1)
    
print("tae_SC " + tae_SC)



# CON CONDICIONES

tin_resto_plazo_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[20]")))
tin_resto_plazo_CC =  tin_resto_plazo_CC.text
print("tin_resto_plazo_CC " + tin_resto_plazo_CC) #Euríbor +1,65 %


tae_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[3]")))
tae_CC =  tae_CC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_CC)
if match:
    tae_CC = match.group(1)
    
print("tae_CC " + tae_CC)

opciones = []

opcion = {
    "nombre" : nombre_hipoteca,
    "tin_6_meses" : tin_6_meses,
    "tin_resto_plazo_SC" : tin_resto_plazo_SC,
    "tae_SC" : tae_SC,
    "tin_resto_plazo_CC" : tin_resto_plazo_CC,
    "tae_CC" : tae_CC,

}
opciones.append(opcion)

#################################################### 
#######           SEGUNDA OPCION            ########
####################################################


nombre_hipoteca = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "div[class='col-6 pl-1 pl-lg-3 ng-star-inserted'] p[class='h5 pl-2 pl-md-3'] strong")))
nombre_hipoteca = nombre_hipoteca.text
nombre_hipoteca = nombre_hipoteca.replace('\n', ' ')
print("nombre_hipoteca " + nombre_hipoteca) # Hipoteca Variable Bonificada

tin_6_meses = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[29]")))
tin_6_meses =  tin_6_meses.text
print("tin_6_meses " + tin_6_meses)# 1,65 %



# SIN CUMPLIR CONDICIONES

tin_resto_plazo_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[33]")))
tin_resto_plazo_SC =  tin_resto_plazo_SC.text
print("tin_resto_plazo_SC " + tin_resto_plazo_SC) 

tae_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[4]")))
tae_SC =  tae_SC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_SC)
if match:
    tae_SC = match.group(1)
    
print("tae_SC " + tae_SC)



# CON CONDICIONES

tin_resto_plazo_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[37]")))
tin_resto_plazo_CC =  tin_resto_plazo_CC.text
print("tin_resto_plazo_CC " + tin_resto_plazo_CC) #Euríbor +1,65 %


tae_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[6]")))
tae_CC =  tae_CC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_CC)
if match:
    tae_CC = match.group(1)
    
print("tae_CC " + tae_CC)

opcion = {
    "nombre" : nombre_hipoteca,
    "tin_6_meses" : tin_6_meses,
    "tin_resto_plazo_SC" : tin_resto_plazo_SC,
    "tae_SC" : tae_SC,
    "tin_resto_plazo_CC" : tin_resto_plazo_CC,
    "tae_CC" : tae_CC,

}
opciones.append(opcion)

print(opciones)
driver.get("https://www.bancosantander.es/particulares/hipotecas/hipoteca-mixta")

ir_hipoteca_mixta = WebDriverWait(driver,5).until(EC.element_to_be_clickable((By.XPATH,"(//a[@data-event-category='product'])[1]"))).click()

iframe = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "iFrameResizer0")))

driver.switch_to.frame(iframe)



boton_calcular = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.ID, "button_CALCULA TU CUOTA")))
boton_calcular.click()

#################################################### 
########           Opcion Mixta            #########
####################################################


nombre_hipoteca = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "div[class='col-6 pr-1 pr-lg-3 ng-star-inserted'] p[class='h5 pl-2 pl-md-3'] strong")))
nombre_hipoteca = nombre_hipoteca.text
nombre_hipoteca = nombre_hipoteca.replace('\n', ' ')
print("nombre_hipoteca " + nombre_hipoteca) # Hipoteca Variable Bonificada

tin_6_meses = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[12]")))
tin_6_meses =  tin_6_meses.text
print("tin_6_meses " + tin_6_meses)# 1,65 %



# SIN CUMPLIR CONDICIONES

tin_2_años_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[16]")))
tin_2_años_SC =  tin_2_años_SC.text
print("tin_2_años " + tin_2_años_SC) 

tin_resto_plazo_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[20]")))
tin_resto_plazo_SC =  tin_resto_plazo_SC.text
print("tin_resto_plazo_SC " + tin_resto_plazo_SC) 

tae_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[1]")))
tae_SC =  tae_SC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_SC)
if match:
    tae_SC = match.group(1)
    
print("tae_SC " + tae_SC)



# CON CONDICIONES
tin_2_años_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[24]")))
tin_2_años_CC =  tin_2_años_CC.text
print("tin_2_años_CC " + tin_2_años_CC) 

tin_resto_plazo_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[28]")))
tin_resto_plazo_CC =  tin_resto_plazo_CC.text
print("tin_resto_plazo_CC " + tin_resto_plazo_CC) #Euríbor +1,65 %


tae_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[3]")))
tae_CC =  tae_CC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_CC)
if match:
    tae_CC = match.group(1)
    
print("tae_CC " + tae_CC)

opcion = {
    "nombre" : nombre_hipoteca,
    "tin_6_meses" : tin_6_meses,
    "tin_2_años_SC" :  tin_2_años_SC,
    "tin_2_años_CC": tin_2_años_CC,
    "tin_resto_plazo_SC" : tin_resto_plazo_SC,
    "tae_SC" : tae_SC,
    "tin_resto_plazo_CC" : tin_resto_plazo_CC,
    "tae_CC" : tae_CC,

}
opciones.append(opcion)

#################################################### 
########           Opcion Mixta  2          ########
####################################################
nombre_hipoteca = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.CSS_SELECTOR, "div[class='col-6 pl-1 pl-lg-3 ng-star-inserted'] p[class='h5 pl-2 pl-md-3'] strong")))
nombre_hipoteca = nombre_hipoteca.text
nombre_hipoteca = nombre_hipoteca.replace('\n', ' ')
print("nombre_hipoteca " + nombre_hipoteca) # Hipoteca Variable Bonificada

tin_6_meses = WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[37]")))
tin_6_meses =  tin_6_meses.text
print("tin_6_meses " + tin_6_meses)# 1,65 %



# SIN CUMPLIR CONDICIONES

tin_9_años_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[41]")))
tin_9_años_SC =  tin_9_años_SC.text
print("tin_9_años_SC " + tin_9_años_SC) 

tin_resto_plazo_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[45]")))
tin_resto_plazo_SC =  tin_resto_plazo_SC.text
print("tin_resto_plazo_SC " + tin_resto_plazo_SC) 

tae_SC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[4]")))
tae_SC =  tae_SC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_SC)
if match:
    tae_SC = match.group(1)
    
print("tae_SC " + tae_SC)



# CON CONDICIONES
tin_9_años_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[49]")))
tin_9_años_CC =  tin_9_años_CC.text
print("tin_9_años_CC " + tin_9_años_CC) 

tin_resto_plazo_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12'])[53]")))
tin_resto_plazo_CC =  tin_resto_plazo_CC.text
print("tin_resto_plazo_CC " + tin_resto_plazo_CC) #Euríbor +1,65 %


tae_CC =  WebDriverWait(driver, 10).until(EC.presence_of_element_located((By.XPATH, "(//div[@class='col-12 text-md-center ng-star-inserted'])[6]")))
tae_CC =  tae_CC.text

regex = r"(\d+,\d+ %)"
match = re.search(regex, tae_CC)
if match:
    tae_CC = match.group(1)
    
print("tae_CC " + tae_CC)

opcion = {
    "nombre" : nombre_hipoteca,
    "tin_6_meses" : tin_6_meses,
    "tin_2_años_SC" :  tin_2_años_SC,
    "tin_2_años_CC": tin_2_años_CC,
    "tin_resto_plazo_SC" : tin_resto_plazo_SC,
    "tae_SC" : tae_SC,
    "tin_resto_plazo_CC" : tin_resto_plazo_CC,
    "tae_CC" : tae_CC,

}
opciones.append(opcion)
for opt in opciones:
    print("opcion" )
print(opciones)


time.sleep(60)



