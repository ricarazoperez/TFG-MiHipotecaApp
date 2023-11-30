from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support.ui import Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.action_chains import ActionChains
from selenium.common.exceptions import TimeoutException
import time
import re
import locale
import requests
from datetime import date, datetime
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from bs4 import BeautifulSoup
from flask import Flask, request, jsonify,redirect, url_for
from flask_apscheduler import APScheduler
import firebase_admin
from firebase_admin import credentials,firestore
from ansible_runner import run
import getpass
import json
import subprocess
from ansible.parsing.vault import VaultLib
clave_vault = getpass.getpass("Introduce la clave de Ansible Vault: ")
archivo_encriptado = '/srv/home/salejo/TFG/mihipotecaapp-4b30a-firebase-adminsdk-uubno-841ef0be20.vault'

# Ejecutar el comando de desencriptación utilizando ansible-vault
proceso = subprocess.run(
    ["ansible-vault", "decrypt", archivo_encriptado, "--output=-"],
    input=clave_vault.encode(),
    capture_output=True,
)

# Verificar el resultado del proceso
if proceso.returncode == 0:
    contenido_desencriptado = proceso.stdout.decode()

    # Convertir el contenido desencriptado a formato JSON
    contenido_json = json.loads(contenido_desencriptado)

    # Aquí puedes trabajar con el contenido JSON como desees
    credentials_json = json.loads(contenido_desencriptado)
    credentials_firebase=credentials.Certificate(credentials_json)

    firebase_admin.initialize_app(credentials_firebase)
    db = firestore.client()

else:
    print("Ha ocurrido un error al desencriptar el archivo.")


array_prueba = []
ofertas_fija = []
euriborHist= []
ofertas_var_mixta = []

def euriborDiario():
    euriborHist.clear()
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    options.binary_location = "/srv/home/ricarazo/TFG/opt/google/chrome/google-chrome"
    chrome_driver_path = '/srv/home/ricarazo/TFG/chromedriver'

    service = Service(chrome_driver_path)
    driver = webdriver.Chrome(service=service, options=options)
    
    
    
    driver.get("https://www.euribor.com.es/")
    euribor=driver.find_element(By.XPATH,'//*[@id="soledad_wrapper"]/div[1]/div/div/div/div[2]/p/b').text
    
    year = str(date.today().year)
    # Configurar locale al horario español
    locale.setlocale(locale.LC_ALL, 'es_ES.UTF-8')  
    mes = datetime.now().strftime("%B").capitalize()
    
    patron = r'([-+]?)([\d\.]+)%'
    coincidencia = re.search(patron, euribor)

    if coincidencia:
        operador, valor = coincidencia.groups()
        if operador == "-":
            resultado = operador + valor
        else:
            resultado = valor
    resultado=float(resultado)
    print(resultado)
  
    meses = {'Enero': 1, 'Febrero': 2, 'Marzo': 3, 'Abril': 4, 'Mayo': 5, 'Junio': 6, 'Julio': 7, 'Agosto': 8, 'Septiembre': 9, 'Octubre': 10, 'Noviembre': 11, 'Diciembre': 12}
    euri = {
            "anio":year,
            "mes":list(meses.keys())[list(meses.values()).index(meses[mes])],
            "valor" :resultado
            }
    cargar(euri)
    euriborHist.append(euri)

    driver.quit()

def euriborMensual():
    euriborHist.clear()
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    options.binary_location = "/srv/home/ricarazo/TFG/opt/google/chrome/google-chrome"
    chrome_driver_path = '/srv/home/ricarazo/TFG/chromedriver'

    service = Service(chrome_driver_path)
    driver = webdriver.Chrome(service=service, options=options)
   
    driver.get("https://www.euribor.com.es/")
    euribor=driver.find_element(By.XPATH,'//*[@id="soledad_wrapper"]/div[1]/div/div/div/div[2]/p/b').text
    
    year = str(date.today().year)
    locale.setlocale(locale.LC_ALL, 'es_ES.UTF-8')  # Configurar locale en español
    mes = datetime.now().strftime("%B").capitalize()
    
    patron = r'([-+]?)([\d\.]+)%'
    coincidencia = re.search(patron, euribor)
    contador = 0

    for coincidencia in re.finditer(patron, euribor):
        contador += 1
        if contador == 2:
            operador, valor = coincidencia.groups()
            if operador == "-":
                resultado = operador + valor
            else:
                resultado = valor
            resultado = float(resultado)
            print(resultado)
  
    meses = {'Enero': 1, 'Febrero': 2, 'Marzo': 3, 'Abril': 4, 'Mayo': 5, 'Junio': 6, 'Julio': 7, 'Agosto': 8, 'Septiembre': 9, 'Octubre': 10, 'Noviembre': 11, 'Diciembre': 12}
    euri = {
            "anio":year,
            "mes":list(meses.keys())[list(meses.values()).index(meses[mes]-1)],
            "valor" :resultado
            }
    cargar(euri)
    euriborHist.append(euri)

    driver.quit()
    


def euriborHistorico():
    euriborHist.clear()
    
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    options.binary_location = "/srv/home/ricarazo/TFG/opt/google/chrome/google-chrome"
    chrome_driver_path = '/srv/home/ricarazo/TFG/chromedriver'

    service = Service(chrome_driver_path)
    driver = webdriver.Chrome(service=service, options=options)
    driver.get("https://www.idealista.com/news/euribor/historico-diario/")
    filas = driver.find_elements(By.CLASS_NAME, "table__row")
    meses = {'Enero': 1, 'Febrero': 2, 'Marzo': 3, 'Abril': 4, 'Mayo': 5, 'Junio': 6, 'Julio': 7, 'Agosto': 8, 'Septiembre': 9, 'Octubre': 10, 'Noviembre': 11, 'Diciembre': 12}
    columna_actualizar=filas[0].find_elements(By.TAG_NAME, "td")
    last_euribor_value = None
    anio_actual= columna_actualizar[0].text
    mes_actual = meses[columna_actualizar[1].text]
    for fila in filas:
        columnas = fila.find_elements(By.TAG_NAME, "td")
        if len(columnas) >= 2:
            anio = columnas[0].text
            mes_nom = columnas[1].text
            mes = meses[mes_nom]
            valor_porcentaje = columnas[2].text
            valor = valor_porcentaje.replace(",", ".").strip("%")

            if anio != anio_actual:
                mes_actual = 12
                anio_actual = anio

            # Actualizar el euribor mensual
            if mes_actual != mes:
                for mes_faltante in range( mes+1,mes_actual+1):
                    euri = {
                        "anio": anio,
                        "mes": list(meses.keys())[list(meses.values()).index(mes_faltante)],
                        "valor": valor
                    }
                    euriborHist.append(euri)
                mes_actual = mes


            # Guardar el valor actual
            euri = {
                "anio": anio,
                "mes": mes_nom,
                "valor": valor
            }
            euriborHist.append(euri)

            mes_actual -= 1
    driver.quit()
    
def cargar(euri):
     # Realizar una consulta en la colección 'euribor' con los criterios de búsqueda
    query = db.collection('euribor').where(field_path='anio', op_string='==', value=euri['anio']).where(field_path='mes', op_string='==', value=euri['mes'])
    #query = db.collection('euribor').where('anio', '==', euri['anio']).where('mes', '==', euri['mes'])
    docs = query.stream()

    # Verificar si hay algún documento que coincida
    doc_exists = False
    for doc in docs:
        doc_exists = True
        doc_id = doc.id
        break

    # Si existe un documento coincidente, actualizarlo
    if doc_exists:
        doc_ref = db.collection('euribor').document(doc_id)
        doc_ref.update(euri)
    # De lo contrario, agregar un nuevo documento
    else:
        doc_ref = db.collection('euribor').document()
        doc_ref.set(euri)
        
    doc_ref.set(euri, merge=True)


app = Flask(__name__)
scheduler=APScheduler()
scheduler.add_job(id="euribor-historico", func=euriborHistorico, trigger="cron", day="1", hour="11")
scheduler.add_job(id="euribor_diario", func=euriborDiario, trigger="cron", day_of_week="mon-fri", hour="12")
scheduler.init_app(app)
scheduler.start()

@app.route("/")
def index():
    return "Pagina principal"

@app.route('/EuriborHistorico')
def EuriborHistorico():
    euriborHistorico()
    return jsonify({"Eur_dos_ult_meses" : euriborHist})

@app.route('/Euribor')
def Euribor():
    euriborMensual()
    return jsonify({"Eur_dos_ult_meses" : euriborHist})

@app.route('/EuriborDiario')
def Euribor_daily():
    euriborDiario()
    return jsonify({"Eur_diario" : euriborHist})



@app.route('/pruebaArray', methods=['POST'])
def pruebaArray():
    ofertas_fija.clear()
    ofertas_var_mixta.clear()
    datos = request.get_json()
    print(datos)
    extraccion(datos)
    print(ofertas_fija)
    return jsonify({"fija" : ofertas_fija, "var_mixta" : ofertas_var_mixta})

@app.route('/pruebaArray')
def pruebaArray2():
    ofertas_fija.clear()
    ofertas_var_mixta.clear()
    comunidad_autonoma = request.args.get('comunidad_autonoma')
    tipo_vivienda = request.args.get('tipo_vivienda')
    antiguedad_vivienda = request.args.get('antiguedad_vivienda')
    precio_vivienda = request.args.get('precio_vivienda')
    cantidad_abonada = request.args.get('cantidad_abonada')
    plazo_anios = request.args.get('plazo_anios')
    ingresos = request.args.get('ingresos')
    detalles = request.args.get('detalles')
    datos = {
        'comunidad_autonoma': comunidad_autonoma,
        'tipo_vivienda': tipo_vivienda,
        'antiguedad_vivienda': antiguedad_vivienda,
        'precio_vivienda': precio_vivienda,
        'cantidad_abonada': cantidad_abonada,
        'plazo_anios': plazo_anios,
        'ingresos': ingresos,
        'detalles': detalles
    }

    print(datos)
    extraccion(datos)
    return jsonify({"fija": ofertas_fija, "var_mixta": ofertas_var_mixta})
    
def extraccion(datos):
    print("Arranca")
    options = Options()
    options.add_argument('--headless')
    options.add_argument('--no-sandbox')
    options.add_argument('--disable-dev-shm-usage')
    options.add_argument('--start-maximized')
    options.binary_location = "/srv/home/ricarazo/TFG/opt/google/chrome/google-chrome"
    chrome_driver_path = '/srv/home/ricarazo/TFG/chromedriver'
    url="https://www.rastreator.com/finanzas"
    service = Service(chrome_driver_path)
    driver = webdriver.Chrome(service=service, options=options)
    driver.get(url)
    btn_cookies = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,'//*[@id="cookies"]/div/div[2]/button[2]'))).click()
    time.sleep(2)

    btn_comparar = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//img[@alt='Hipotecas - Rastreator.com'])[1]"))).click()

    #Habitual o segunda vivienda
    var_tipo_compra=datos["tipo_vivienda"]
    var_tipo_compra = "Habitual"
    var_tipo_compra = var_tipo_compra.lower()
    select_tipo = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//select[@name='Purpose'])[1]")))

    select_tipo = Select(select_tipo)
    options = select_tipo.options

    for opt in options:
        opcion = opt.text
        opcion = opcion.lower()
        if var_tipo_compra in opcion:
            select_tipo.select_by_visible_text(opt.text)

    time.sleep(2)

    var_precio = datos["precio_vivienda"]
    input_precio = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//input[@placeholder='Ej: 250.000€'])[1]")))
    input_precio.send_keys(Keys.CONTROL + "a")
    input_precio.send_keys(var_precio)

    var_ahorros =datos["cantidad_abonada"]
    input_ahorros = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//input[@placeholder='Ej: 60.000€'])[1]")))
    input_ahorros.send_keys(Keys.CONTROL + "a")
    input_ahorros.send_keys(var_ahorros)

    slider_plazo = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//span[@role='slider'])[1]")))
    var_plazo = int(datos["plazo_anios"])

    distancia = (30 - var_plazo)*18
    ActionChains(driver).drag_and_drop_by_offset(slider_plazo,-distancia,0).perform()


    var_tipo_vivienda = datos["antiguedad_vivienda"]
    select_tipo_vivienda = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//select[@name='Type'])[1]")))

    select_tipo_vivienda = Select(select_tipo_vivienda)
    options = select_tipo_vivienda.options

    for opt in options:
        opcion = opt.text
        opcion = opcion.lower()
        if var_tipo_vivienda in opcion:
            select_tipo_vivienda.select_by_visible_text(opt.text)

    var_provincia = datos["comunidad_autonoma"]
    var_provincia = var_provincia.lower()
    select_provincia = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//select[@name='PostalCode_dropdown'])[1]")))

    select_provincia = Select(select_provincia)
    options = select_provincia.options

    for opt in options:
        opcion = opt.text
        opcion = opcion.lower()
        if var_provincia in opcion:
            select_provincia.select_by_visible_text(opt.text)

    var_busqueda = "elegida"
    var_busqueda = var_busqueda.lower()
    select_busqueda = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//select[@name='MortgageSignIntent'])[1]")))

    select_busqueda = Select(select_busqueda)
    options = select_busqueda.options

    for opt in options:
        opcion = opt.text
        opcion = opcion.lower()
        if var_busqueda in opcion:
            select_busqueda.select_by_visible_text(opt.text)

    var_ingresos = datos["ingresos"]
    input_ingresos = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//input[@placeholder='Ej: 3.000€'])[1]")))
    input_ingresos.send_keys(Keys.CONTROL + "a")
    input_ingresos.send_keys(var_ingresos)


    check_terms = WebDriverWait(driver,10).until(EC.presence_of_element_located((By.XPATH,"(//span[@class='checkmark'])[1]"))).click()

    time.sleep(2)
    try:
        btn_calcular = WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CSS_SELECTOR, ".cj-btn.ng-tns-c115-1")))
        btn_calcular.click()
    except TimeoutException:
        print("No se pudo encontrar el botón de cálculo")
   
    time.sleep(10)
    html=driver.page_source
    soup=BeautifulSoup(html,'html.parser')
    # "Beautify" the HTML code
    pretty_html = soup.prettify()
    # Print the "beautified" HTML code to a text file
    with open("output.html", "w", encoding="utf-8") as text_file:
        print(pretty_html, file=text_file)  
    opciones_fija = []
    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")))
    opciones = driver.find_elements(By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")
    if datos["detalles"] == "false":
        pos_btn=5
        try:
            btn_help=WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "help-button-btn.btn.help.asesor.ng-tns-c96-" +str( + pos_btn)+".ng-star-inserted")))
            driver.execute_script("arguments[0].style.visibility='hidden';", btn_help)
            driver.execute_script("arguments[0].style.pointerEvents='none';", btn_help)
            pos_btn=pos_btn +4
        except TimeoutException:
            print("No se pudo encontrar el botón de cálculo")
        for opt in opciones:
            img_src = opt.find_element(By.XPATH, ".//img").get_attribute("src")
            bank_name = img_src.split("/")[-1].split("_")[0]
            bank_name = bank_name.upper()
            div_company = opt.find_element(By.CLASS_NAME,"company-name")
            tipo = div_company.find_element(By.TAG_NAME,"p").text
            tae = opt.find_element(By.CLASS_NAME,"main-value.ng-star-inserted")
            tae_value = tae.text
            tin = opt.find_element(By.CLASS_NAME,"first-value")
            tin = tin.text
            cuota = opt.find_element(By.CLASS_NAME,"fee-block.desktop.ng-star-inserted")
            cuota = cuota.find_elements(By.TAG_NAME, "p")[-1].text
            opcion = {
                "banco" : bank_name,
                "desc" : tipo,
                "tae" : tae_value,
                "tin" : tin,
                "cuota": cuota
            }
            opciones_fija.append(opcion)
            ofertas_fija.append(opcion)
    else:
        
        pos_btn=5
        for i in range(5) :
            time.sleep(2)
            current_url = driver.current_url
            print("URL actual:", current_url)
            opciones = driver.find_elements(By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")
            cantidad_opciones = len(opciones)
            print("Cantidad de opciones:", cantidad_opciones)
            opt=opciones[i]
            print(opt)
            img_src = WebDriverWait(opt, 10).until(EC.visibility_of_element_located((By.XPATH, ".//img"))).get_attribute("src")
            bank_name = img_src.split("/")[-1].split("_")[0]
            bank_name = bank_name.upper()
            print(bank_name)
            div_company = opt.find_element(By.CLASS_NAME,"company-name")
            tipo = div_company.find_element(By.TAG_NAME,"p").text
            tae = opt.find_element(By.CLASS_NAME,"main-value.ng-star-inserted")
            tae_value = tae.text
            tin = opt.find_element(By.CLASS_NAME,"first-value")
            tin = tin.text
            cuota = opt.find_element(By.CLASS_NAME,"fee-block.desktop.ng-star-inserted")
            cuota = cuota.find_elements(By.TAG_NAME, "p")[-1].text
            detalles = opt.find_element(By.TAG_NAME, "a")
            try:
                btn_help=WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "help-button-btn.btn.help.asesor.ng-tns-c96-" +str( + pos_btn)+".ng-star-inserted")))
                driver.execute_script("arguments[0].style.visibility='hidden';", btn_help)
                driver.execute_script("arguments[0].style.pointerEvents='none';", btn_help)
            except TimeoutException:
                print("No se pudo encontrar el botón de cálculo")
            detalles.click()
            time.sleep(2)
            detalles_tae = driver.find_element(By.XPATH, "//div[@class='row']//app-more-details-accordion-tae")
            desplegar = detalles_tae.find_element(By.TAG_NAME,"h5")
            desplegar.click()
            time.sleep(1)
            div_principal = driver.find_element(By.XPATH,"//div[@class='collapsed active']")
            div_anidados = div_principal.find_elements(By.CLASS_NAME,"text-block.ng-star-inserted")
            ultimo_div = div_anidados[-1]
            vinculaciones = []
            lineas_vinculacion = ultimo_div.find_elements(By.TAG_NAME,"p")
            for linea in lineas_vinculacion:
                vinculaciones.append(linea.text)
            prueba = ""
            for vinc in vinculaciones:
                prueba = prueba + vinc + "\n"

            print(prueba)
            opcion = {
                "banco" : bank_name,
                "desc" : tipo,
                "tae" : tae_value,
                "tin" : tin,
                "cuota": cuota,
                "vinculaciones" : prueba 
            }
            opciones_fija.append(opcion)
            ofertas_fija.append(opcion)
            pos_btn=pos_btn +4
            btn_regresar=driver.find_element(By.XPATH,"/html/body/app-root/div/app-more-details/div/div[1]/i").click()
         

    opciones_variable_mixta = []
    try:
        btn_help=WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "help-button-btn.btn.help.asesor.ng-tns-c96-" +str( + pos_btn)+".ng-star-inserted")))
        driver.execute_script("arguments[0].style.visibility='hidden';", btn_help)
        driver.execute_script("arguments[0].style.pointerEvents='none';", btn_help)
        pos_btn=pos_btn +4
    except TimeoutException:
        print("No se pudo encontrar el botón de cálculo")
    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.XPATH, "(//p[@class='filter-header'][normalize-space()='Hipoteca Variable'])[1]"))).click()
    time.sleep(1)
    WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")))
    opciones = driver.find_elements(By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")
    if datos["detalles"] == "false":
        #CargarVariable
        cargar_opciones_sin_detalles(opciones)
        #CargarMixta
        WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.XPATH, "(//p[@class='filter-header'][normalize-space()='Hipoteca Mixta'])[1]"))).click()
        time.sleep(1)
        WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")))
        opciones = driver.find_elements(By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")
        cargar_opciones_sin_detalles(opciones)
    else:
        cont_var = 0
        cont_mix = 0
        i = 0
        #Cargar Variable
        while (cont_var <= 3) :
            time.sleep(1)
            print(i)
            opciones = driver.find_elements(By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")
            opt = opciones[i]
            cont_var +=1
            if(cargar_opciones_con_detalles(opt,driver,pos_btn)):
                pos_btn+=4
            i+=1
        try:
            btn_help=WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "help-button-btn.btn.help.asesor.ng-tns-c96-" +str( + pos_btn)+".ng-star-inserted")))
            driver.execute_script("arguments[0].style.visibility='hidden';", btn_help)
            driver.execute_script("arguments[0].style.pointerEvents='none';", btn_help)
            pos_btn+=4
        except TimeoutException:
            print("No se pudo encontrar el botón de cálculo")
        #Hipoteca Mixta 
        i=0
        WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.XPATH, "(//p[@class='filter-header'][normalize-space()='Hipoteca Mixta'])[1]"))).click()
        time.sleep(1)
        WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")))
        while (cont_mix <= 3) :
            time.sleep(1)
            print(i)
            opciones = driver.find_elements(By.CLASS_NAME, "price-card.animated.fadeIn.ng-star-inserted")
            opt = opciones[i]
            cont_mix +=1
            if(cargar_opciones_con_detalles(opt,driver,pos_btn)):
                pos_btn+=4
            i+=1
                
               
                    
def cargar_opciones_sin_detalles(opciones):
     for opt in opciones:
            img_src = opt.find_element(By.XPATH, ".//img").get_attribute("src")
            bank_name = img_src.split("/")[-1].split("_")[0]
            bank_name = bank_name.upper()
            div_company = opt.find_element(By.CLASS_NAME,"company-name")
            tipo = div_company.find_element(By.TAG_NAME,"p").text
            tae = opt.find_element(By.CLASS_NAME,"main-value.ng-star-inserted")
            tae_value = tae.text
            tin_x_anios = opt.find_element(By.CLASS_NAME,"first-value")
            tin_x_anios = tin_x_anios.text
            div_prices = opt.find_element(By.CLASS_NAME,"tae-block.desktop.ng-star-inserted")
            tin_resto = div_prices.find_elements(By.TAG_NAME, "p")[-1].text
            tin_resto = tin_resto.replace("Resto años: ", "")
            cuota_x_anios = opt.find_element(By.CLASS_NAME,"first-value.ng-star-inserted")
            cuota_x_anios = cuota_x_anios.text
            div_cuotas = opt.find_element(By.CLASS_NAME,"fee-block.desktop.ng-star-inserted")
            cuota_resto = div_cuotas.find_elements(By.TAG_NAME, "p")[-1].text
            cuota_resto = cuota_resto.replace("Resto años: ", "")
            opcion = {
                "banco" : bank_name,
                "desc" : tipo,
                "tae" : tae_value,
                "tin_x_anios" : tin_x_anios,
                "tin_resto" : tin_resto,
                "cuota_x_anios" : cuota_x_anios,
                "cuota_resto": cuota_resto
            }
            ofertas_var_mixta.append(opcion)
            
def cargar_opciones_con_detalles(opt,driver,pos_btn):
    div_company = opt.find_element(By.CLASS_NAME,"company-name")
    tipo = div_company.find_element(By.TAG_NAME,"p").text
    img_src = opt.find_element(By.XPATH, ".//img").get_attribute("src")
    bank_name = img_src.split("/")[-1].split("_")[0]
    bank_name = bank_name.upper()
    tae = opt.find_element(By.CLASS_NAME,"main-value.ng-star-inserted")
    tae_value = tae.text
    tin_x_anios = opt.find_element(By.CLASS_NAME,"first-value")
    tin_x_anios = tin_x_anios.text
    div_prices = opt.find_element(By.CLASS_NAME,"tae-block.desktop.ng-star-inserted")
    tin_resto = div_prices.find_elements(By.TAG_NAME, "p")[-1].text
    tin_resto = tin_resto.replace("Resto años: ", "")
    cuota_x_anios = opt.find_element(By.CLASS_NAME,"first-value.ng-star-inserted")
    cuota_x_anios = cuota_x_anios.text
    div_cuotas = opt.find_element(By.CLASS_NAME,"fee-block.desktop.ng-star-inserted")
    cuota_resto = div_cuotas.find_elements(By.TAG_NAME, "p")[-1].text
    cuota_resto = cuota_resto.replace("Resto años: ", "")
    detalles = opt.find_element(By.TAG_NAME, "a")
    try:
        encontrado=True
        btn_help=WebDriverWait(driver, 10).until(EC.visibility_of_element_located((By.CLASS_NAME, "help-button-btn.btn.help.asesor.ng-tns-c96-" +str( + pos_btn)+".ng-star-inserted")))
        driver.execute_script("arguments[0].style.visibility='hidden';", btn_help)
        driver.execute_script("arguments[0].style.pointerEvents='none';", btn_help)
    except TimeoutException:
        encontrado=False
        print("No se pudo encontrar el botón de cálculo")
    detalles.click()
    time.sleep(2)
    detalles_tae = driver.find_element(By.XPATH, "//div[@class='row']//app-more-details-accordion-tae")
    desplegar = detalles_tae.find_element(By.TAG_NAME,"h5")
    desplegar.click()
    time.sleep(1)
    div_principal = driver.find_element(By.XPATH,"//div[@class='collapsed active']")
    div_anidados = div_principal.find_elements(By.CLASS_NAME,"text-block.ng-star-inserted")
    ultimo_div = div_anidados[-2]
    vinculaciones = []
    lineas_vinculacion = ultimo_div.find_elements(By.TAG_NAME,"p")
    for linea in lineas_vinculacion:
        vinculaciones.append(linea.text)
    prueba = ""
    for vinc in vinculaciones:
        prueba = prueba + vinc + "\n"

    opcion = {
        "banco" : bank_name,
        "desc" : tipo,
        "tae" : tae_value,
        "tin_x_anios" : tin_x_anios,
        "tin_resto" : tin_resto,
        "cuota_x_anios" : cuota_x_anios,
        "cuota_resto": cuota_resto,
        "vinculaciones" : prueba
    }
    print(opcion)
    ofertas_var_mixta.append(opcion)
    btn_regresar = driver.find_element(By.XPATH,"//button[normalize-space()='Volver a Resultados']").click()
    return encontrado         


if __name__ == '__main__':
    app.run(host='147.96.81.245', port=5000)
    