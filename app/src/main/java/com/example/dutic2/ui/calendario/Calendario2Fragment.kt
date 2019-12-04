package com.example.dutic2.ui.calendario


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dutic2.R
import com.example.dutic2.models.Curso
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.calendar_day_legend.view.*
import kotlinx.android.synthetic.main.calendario2_fragment.*
import kotlinx.android.synthetic.main.example_3_calendar_day.view.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import java.util.*


data class Event(val id: String, val text: String, val date: LocalDate)

class Example3EventsAdapter(val onClick: (Event) -> Unit) :
    RecyclerView.Adapter<Example3EventsAdapter.Example3EventsViewHolder>() {

    val events = mutableListOf<Event>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Example3EventsViewHolder {
        return Example3EventsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.example_3_event_item_view,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(viewHolder: Example3EventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class Example3EventsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(event: Event) {
            itemView.findViewById<TextView>(R.id.itemEventText).text = event.text
        }
    }

}

class Calendario2Fragment : BaseFragment(), HasBackButton {

    override val titleRes: Int?
        get() = null

    private val eventsAdapter = Example3EventsAdapter {
        AlertDialog.Builder(requireContext())
            .setMessage("borerar")
            .setPositiveButton("delete") { _, _ ->
                deleteEvent(it)
            }
            .setNegativeButton("cerrar", null)
            .show()
    }

    private val inputDialog by lazy {
        val editText = AppCompatEditText(requireContext())
        val layout = FrameLayout(requireContext()).apply {
            // Setting the padding on the EditText only pads the input area
            // not the entire EditText so we wrap it in a FrameLayout.
            val padding = dpToPx(20, requireContext())
            setPadding(padding, padding, padding, padding)
            addView(editText, FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        }
        AlertDialog.Builder(requireContext())
            .setTitle("Inserte Recordatorio")
            .setView(layout)
            .setPositiveButton("guardar") { _, _ ->
                saveEvent(editText.text.toString())
                // Prepare EditText for reuse.
                editText.setText("")
            }
            .setNegativeButton("cerrar", null)
            .create()
//            .apply {
//                setOnShowListener {
//                    // Show the keyboard
//                    editText.requestFocus()
//                    context.inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
//                }
//                setOnDismissListener {
//                    // Hide the keyboard
//                    context.inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
//                }
    }
    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private lateinit var curso: Curso
    private lateinit var calendarioReferencia: DocumentReference
    private val user = FirebaseAuth.getInstance().currentUser
    private val titleSameYearFormatter = DateTimeFormatter.ofPattern("MMMM")
    private val titleFormatter = DateTimeFormatter.ofPattern("MMM yyyy")
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")
    private var events = mutableMapOf<LocalDate, List<Event>>()
    val gson = Gson()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        curso = arguments?.getParcelable<Curso>("curso") as Curso
        return inflater.inflate(R.layout.calendario2_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getFechasGuardadas()

        exThreeRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        exThreeRv.adapter = eventsAdapter
        exThreeRv.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        val daysOfWeek = daysOfWeekFromLocale()
        val currentMonth = YearMonth.now()
        exThreeCalendar.setup(
            currentMonth.minusMonths(10),
            currentMonth.plusMonths(10),
            daysOfWeek.first()
        )
        exThreeCalendar.scrollToMonth(currentMonth)

        if (savedInstanceState == null) {
            exThreeCalendar.post {
                // Show today's events initially.
                selectDate(today)
            }
        }

        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val textView = view.exThreeDayText
            val dotView = view.exThreeDotView

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectDate(day.date)
                    }
                }
            }
        }
        exThreeCalendar.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                val textView = container.textView
                val dotView = container.dotView

                textView.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    textView.makeVisible()
                    when (day.date) {
                        today -> {
                            textView.setTextColorRes(R.color.example_3_white)
                            textView.setBackgroundResource(R.drawable.example_3_today_bg)
                            dotView.makeInVisible()
                        }
                        selectedDate -> {
                            textView.setTextColorRes(R.color.example_3_blue)
                            textView.setBackgroundResource(R.drawable.example_3_selected_bg)
                            dotView.makeInVisible()
                        }
                        else -> {
                            textView.setTextColorRes(R.color.example_3_black)
                            textView.background = null
                            dotView.isVisible = events[day.date].orEmpty().isNotEmpty()
                        }
                    }
                } else {
                    textView.makeInVisible()
                    dotView.makeInVisible()
                }
            }
        }

        exThreeCalendar.monthScrollListener = {
            requireActivity().toolbar.title = if (it.year == today.year) {
                titleSameYearFormatter.format(it.yearMonth)
            } else {
                titleFormatter.format(it.yearMonth)
            }

            // Select the first day of the month when
            // we scroll to a new month.
            selectDate(it.yearMonth.atDay(1))
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.legendLayout
        }
        exThreeCalendar.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                if (container.legendLayout.tag == null) {
                    container.legendLayout.tag = month.yearMonth
                    container.legendLayout.children.map { it as TextView }
                        .forEachIndexed { index, tv ->
                            tv.text = daysOfWeek[index].name.first().toString()
                            tv.setTextColorRes(R.color.example_3_black)
                        }
                }
            }
        }

        exThreeAddButton.setOnClickListener {
            inputDialog.show()
        }
    }

    private fun getFechasGuardadas() {
        calendarioReferencia =
            FirebaseFirestore.getInstance().document("/usuarios/${user?.uid}/cursos/${curso.uid}")
        calendarioReferencia.get().addOnSuccessListener {
            try {
                val s = it.data!!["calendario"].toString()
                val temp: MutableMap<String, List<Event>> =
                    gson.fromJson(s, object : TypeToken<MutableMap<String, List<Event>>>() {}.type)
                temp.forEach { entry ->
                    val date = LocalDate.parse(entry.key)
                    events[date] = entry.value
                    exThreeCalendar.notifyDateChanged(date)
                    updateAdapterForDate(date)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Log.e("Eventos guardados", " Value : ${it?.data}")
        }
    }

    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { exThreeCalendar.notifyDateChanged(it) }
            exThreeCalendar.notifyDateChanged(date)
            updateAdapterForDate(date)
        }
    }

    private fun saveEvent(text: String) {
        if (text.isBlank()) {
            Toast.makeText(requireContext(), "Inserte valor", Toast.LENGTH_LONG)
                .show()
        } else {
            selectedDate?.let {
                events[it] =
                    events[it].orEmpty().plus(Event(UUID.randomUUID().toString(), text, it))
                updateAdapterForDate(it)
            }
        }
    }

    private fun deleteEvent(event: Event) {
        val date = event.date
        events[date] = events[date].orEmpty().minus(event)
        updateAdapterForDate(date)
    }

    private fun updateAdapterForDate(date: LocalDate) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty())
        eventsAdapter.notifyDataSetChanged()
        exThreeSelectedDateText.text = selectionFormatter.format(date)
    }

    fun View.makeVisible() {
        visibility = View.VISIBLE
    }

    fun View.makeInVisible() {
        visibility = View.INVISIBLE
    }

    fun View.makeGone() {
        visibility = View.GONE
    }

    fun dpToPx(dp: Int, context: Context): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()

    internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
        return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
    }

    private fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

    internal fun TextView.setTextColorRes(@ColorRes color: Int) =
        setTextColor(context.getColorCompat(color))

    private fun daysOfWeekFromLocale(): Array<DayOfWeek> {
        val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
        var daysOfWeek = DayOfWeek.values()
        // Order `daysOfWeek` array so that firstDayOfWeek is at index 0.
        if (firstDayOfWeek != DayOfWeek.MONDAY) {
            val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
            val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
            daysOfWeek = rhs + lhs
        }
        return daysOfWeek
    }

    override fun onStop() {
        Log.e("Map values", "Keys : ${events.keys}, values: ${events.values} y map : $events")


        calendarioReferencia.set(
            hashMapOf("calendario" to gson.toJson(events,
                object : TypeToken<MutableMap<LocalDate, List<Event>>>() {}.type
            )
            ), SetOptions.merge()
        )
        super.onStop()

    }
}